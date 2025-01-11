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
        List<TimeSlot> timeSlots = timeSlotRepository.getTeacherTimeSlots(teacher, now, plusMonth);
        log.info("Get time slot list ({}) for teacher {}", timeSlots.size(), teacher);
        return timeSlots;
    }

    @Transactional
    public TimeSlot addTimeSlot(UUID teacherId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Time slot cannot start in the past");
        }

        if (Duration.between(startTime, endTime).toHours() > 2) {
            throw new IllegalArgumentException("Time slot duration must not exceed 2 hours");
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
}
