package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;

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

    @Transactional(readOnly = true)
    public List<TimeSlot> getTeacherTimeSlotsInNextMonth(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusMonth = now.plusMonths(1);
        List<TimeSlot> timeSlots = timeSlotRepository.getTeacherAvailableTimeSlots(teacher, now, plusMonth);
        log.info("Get available time slot list ({}) for teacher {}", timeSlots.size(), teacher);
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

        List<TimeSlot> overlapping = timeSlotRepository.findOverlappingTimeSlots(teacher, startTime, endTime);
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping times slots");
        }

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
}
