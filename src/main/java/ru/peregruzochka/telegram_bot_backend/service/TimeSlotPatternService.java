package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.DayOfWeek;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotPatternRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotPatternRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSlotPatternService {
    private final TimeSlotPatternRepository timeSlotPatternRepository;
    private final TeacherRepository teacherRepository;
    private final GroupTimeSlotPatternRepository groupTimeSlotPatternRepository;

    @Transactional
    public TimeSlotPattern createTimeSlotPattern(DayOfWeek dayOfWeek, LocalTime startTime, UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        checkPatternOverlapping(dayOfWeek, startTime, teacher);

        TimeSlotPattern timeSlotPattern = TimeSlotPattern.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(45))
                .teacher(teacher)
                .build();

        TimeSlotPattern savedPattern = timeSlotPatternRepository.save(timeSlotPattern);
        log.info("Created TimeSlotPattern: {}", savedPattern);
        return savedPattern;
    }

    @Transactional(readOnly = true)
    public void checkPatternOverlapping(DayOfWeek dayOfWeek, LocalTime startTime, Teacher teacher) {
        LocalTime endTime = startTime.plusMinutes(45);

        List<TimeSlotPattern> overlapping = timeSlotPatternRepository
                .findOverLappingPatterns(teacher, dayOfWeek, startTime, endTime);
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping patterns found");
        }

        List<GroupTimeSlotPattern> groupOverlapping = groupTimeSlotPatternRepository
                .findOverLappingPatterns(teacher, dayOfWeek, startTime, endTime);
        if (!groupOverlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping patterns found");
        }
    }
}
