package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.DayOfWeek;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.repository.GroupLessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotPatternRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;

import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupTimeSlotPatternService {

    private final GroupTimeSlotPatternRepository groupTimeSlotPatternRepository;
    private final TeacherRepository teacherRepository;
    private final TimeSlotPatternService timeSlotPatternService;
    private final GroupLessonRepository groupLessonRepository;

    @Transactional
    public GroupTimeSlotPattern createGroupTimeSlotPattern(DayOfWeek dayOfWeek, LocalTime startTime, UUID teacherId, UUID lessonId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        timeSlotPatternService.checkPatternOverlapping(dayOfWeek, startTime, teacher);

        GroupLesson lesson = groupLessonRepository.findById(lessonId).orElseThrow(
                () -> new IllegalArgumentException("Lesson not found")
        );

        GroupTimeSlotPattern groupTimeSlotPattern = GroupTimeSlotPattern.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(45))
                .teacher(teacher)
                .groupLesson(lesson)
                .build();

        GroupTimeSlotPattern savedGroupTimeSlotPattern = groupTimeSlotPatternRepository.save(groupTimeSlotPattern);
        log.info("Created GroupTimeSlotPattern: {}", savedGroupTimeSlotPattern);
        return savedGroupTimeSlotPattern;
    }
}
