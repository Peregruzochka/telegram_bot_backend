package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
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
    public void fillByPattens(LocalDateTime from, LocalDateTime to) {
        LocalDateTime day = from;
        while (day.isBefore(to.plusDays(1))) {
            DayOfWeek dayOfWeek = day.getDayOfWeek();

        }
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
