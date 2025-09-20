package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotPatternRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotPatternRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<TimeSlotPattern> overlapping = timeSlotPatternRepository.findOverLappingPatterns(dayOfWeek, startTime, endTime);
        List<GroupTimeSlotPattern> groupOverlapping = groupTimeSlotPatternRepository.findOverLappingPatterns(dayOfWeek, startTime, endTime);

        Set<Teacher> teachers = new HashSet<>();
        overlapping.stream().map(TimeSlotPattern::getTeacher).forEach(teachers::add);
        groupOverlapping.stream().map(GroupTimeSlotPattern::getTeacher).forEach(teachers::add);

        if (teachers.size() >= 4) {
            throw new IllegalArgumentException("The number of simultaneously working teachers exceeds 4");
        }

        if (teachers.contains(teacher)) {
                throw new IllegalArgumentException("At this time, the teacher is already working: " + teacher.getName());
        }
    }

    @Transactional
    public void deleteTimeSlotPattern(UUID timeSlotPatternId) {
        if (!timeSlotPatternRepository.existsById(timeSlotPatternId)) {
            throw new IllegalArgumentException("TimeSlotPattern not found");
        }
        timeSlotPatternRepository.deleteById(timeSlotPatternId);
        log.info("Deleted TimeSlotPattern: {}", timeSlotPatternId);
    }

    @Transactional(readOnly = true)
    public List<TimeSlotPattern> getPatternByTeacherAndDayOfWeek(UUID teacherId, DayOfWeek dayOfWeek) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<TimeSlotPattern> patterns = timeSlotPatternRepository.findByTeacherAndDayOfWeek(teacher, dayOfWeek);
        log.info("Found {} patterns", patterns.size());
        return patterns;
    }
}
