package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotPatternRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotPatternRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final TeacherRepository teacherRepository;
    private final GroupTimeSlotRepository groupTimeSlotRepository;
    private final TimeSlotPatternRepository timeSlotPatternRepository;
    private final GroupTimeSlotPatternRepository groupTimeSlotPatternRepository;

    @Transactional(readOnly = true)
    public List<TimeSlot> getTeacherAvailableTimeSlotsInNextMonth(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusMonth = now.plusMonths(1);
        List<TimeSlot> timeSlots = timeSlotRepository.getTeacherAvailableTimeSlots(teacher, now, plusMonth);
        log.info("Get available time slot list in next month ({}) for teacher {}", timeSlots.size(), teacher);
        return timeSlots;
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getTeacherTimeSlotsByDate(UUID teacherId, LocalDate date) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = from.plusDays(1);
        List<TimeSlot> timeSlots = timeSlotRepository.getTeacherAllTimeSlots(teacher, from, to);
        log.info("Get all time slot list ({}) for teacher {}", timeSlots.size(), teacher);
        return timeSlots;
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getTeacherAvailableTimeSlotsByDate(UUID teacherId, LocalDate date) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = from.plusDays(1);
        List<TimeSlot> timeSlots = timeSlotRepository.getTeacherAvailableTimeSlots(teacher, from, to);
        log.info("Get available time slot list  ({}) by date [{}] for teacher {}", timeSlots.size(), date, teacher);
        return timeSlots;
    }

    @Transactional
    public TimeSlot addTimeSlot(TimeSlot newTimeSlot) {
        LocalDateTime startTime = newTimeSlot.getStartTime();
        LocalDateTime endTime = newTimeSlot.getEndTime();
        UUID teacherId = newTimeSlot.getTeacher().getId();

        if (endTime == null) {
            endTime = startTime.plusMinutes(45);
        }

        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Time slot cannot start in the past");
        }

        if (Duration.between(startTime, endTime).toMinutes() != 45) {
            throw new IllegalArgumentException("Time slot duration must not exceed 45 minutes");
        }

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );


        checkOverlapping(teacher, startTime, endTime);

        TimeSlot timeSlot = TimeSlot.builder()
                .teacher(teacher)
                .startTime(startTime)
                .endTime(endTime)
                .isAvailable(true)
                .build();

        TimeSlot savedTimeSlot = timeSlotRepository.save(timeSlot);
        log.info("Saved time slot {}", savedTimeSlot);
        return savedTimeSlot;
    }

    @Transactional
    public void removeTimeSlot(UUID timeslotId) {
        timeSlotRepository.deleteById(timeslotId);
        log.info("Removed time slot {}", timeslotId);
    }

    @Transactional(readOnly = true)
    public TimeSlot getTimeSlot(UUID timeslotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeslotId).orElseThrow(
                () -> new IllegalArgumentException("TimeSlot not found")
        );
        log.info("Get time slot {}", timeSlot);
        return timeSlot;
    }

    @Transactional
    public void fillByPatterns(LocalDate from, LocalDate to) {
        LocalDate day = from;
        while (day.isBefore(to.plusDays(1))) {
            fillIndividualSlots(day);
            fillGroupSlots(day);
            day = day.plusDays(1);
        }
    }

    @Transactional
    public void clear(LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.plusDays(1).atStartOfDay();
        deleteIndividualSlots(fromTime, toTime);
        deleteGroupSlots(fromTime, toTime);
    }

    private void deleteIndividualSlots(LocalDateTime fromTime, LocalDateTime toTime) {
        List<TimeSlot> slots = timeSlotRepository.findTimeSlotsByStartTimeBetween(fromTime, toTime);
        int count = 0;
        for (TimeSlot slot : slots) {
            if (!slot.getIsAvailable()) {
                continue;
            }
            timeSlotRepository.delete(slot);
            count++;
        }
        log.info("Deleted time slot {}", count);
    }

    private void deleteGroupSlots(LocalDateTime fromTime, LocalDateTime toTime) {
        List<GroupTimeSlot> slots = groupTimeSlotRepository.findTimeSlotsByStartTimeBetween(fromTime, toTime);
        int count = 0;
        for (GroupTimeSlot slot : slots) {
            if (!slot.getRegistrations().isEmpty()) {
                continue;
            }
            groupTimeSlotRepository.delete(slot);
            count++;
        }
        log.info("Deleted group time slot {}", count);
    }

    private void fillGroupSlots(LocalDate day) {
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        List<GroupTimeSlotPattern> groupPatternsByDay = groupTimeSlotPatternRepository.findByDayOfWeek(dayOfWeek);
        int count = 0;
        for (GroupTimeSlotPattern pattern : groupPatternsByDay) {
            LocalDateTime slotStartTime = LocalDateTime.of(day, pattern.getStartTime());
            LocalDateTime slotEndTime = LocalDateTime.of(day, pattern.getEndTime());
            Teacher teacher = pattern.getTeacher();
            if (countOverlapping(teacher, slotStartTime, slotEndTime) > 0) {
                continue;
            }
            GroupLesson lesson = pattern.getGroupLesson();
            GroupTimeSlot slot = GroupTimeSlot.builder()
                    .teacher(teacher)
                    .startTime(slotStartTime)
                    .endTime(slotEndTime)
                    .groupLesson(lesson)
                    .build();

            groupTimeSlotRepository.save(slot);
            count++;
        }
        log.info("Fill group slots by day:{} -> {}", day, count);
    }

    private void fillIndividualSlots(LocalDate day) {
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        List<TimeSlotPattern> patternsByDay = timeSlotPatternRepository.findByDayOfWeek(dayOfWeek);
        int count = 0;
        for (TimeSlotPattern pattern : patternsByDay) {
            LocalDateTime slotStartTime = LocalDateTime.of(day, pattern.getStartTime());
            LocalDateTime slotEndTime = LocalDateTime.of(day, pattern.getEndTime());
            Teacher teacher = pattern.getTeacher();
            if (countOverlapping(teacher, slotStartTime, slotEndTime) > 0) {
                continue;
            }

            TimeSlot slot = TimeSlot.builder()
                    .teacher(teacher)
                    .startTime(slotStartTime)
                    .endTime(slotEndTime)
                    .isAvailable(true)
                    .build();

            timeSlotRepository.save(slot);
            count++;
        }
        log.info("Fill individual slots by day:{} -> {}", day, count);
    }

    private int countOverlapping(Teacher teacher, LocalDateTime startTime, LocalDateTime endTime) {
        int slotCount = timeSlotRepository.findOverlappingTimeSlots(teacher, startTime, endTime).size();
        int groupCount = groupTimeSlotRepository.findOverlappingTimeSlots(teacher, startTime, endTime).size();
        return slotCount + groupCount;
    }

    private void checkOverlapping(Teacher teacher, LocalDateTime start, LocalDateTime end) {
        List<TimeSlot> individualOverlapping = timeSlotRepository.findOverlappingTimeSlots(teacher, start, end);
        if (!individualOverlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping times slots");
        }

        List<GroupTimeSlot> groupOverlapping = groupTimeSlotRepository.findOverlappingTimeSlots(teacher, start, end);
        if (!groupOverlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping times slots");
        }
    }


}
