package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.repository.GroupLessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotPatternRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

        checkTeacherLesson(teacher, lesson);

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

    @Transactional
    public void deleteGroupTimeSlotPattern(UUID groupTimeSlotPatternId) {
        if (!groupTimeSlotPatternRepository.existsById(groupTimeSlotPatternId)) {
            throw new IllegalArgumentException("GroupTimeSlotPattern not found");
        }
        groupTimeSlotPatternRepository.deleteById(groupTimeSlotPatternId);
        log.info("Deleted GroupTimeSlotPattern: {}", groupTimeSlotPatternId);
    }

    @Transactional(readOnly = true)
    public List<GroupTimeSlotPattern> getPatternByTeacherAndDayOfWeek(UUID teacherId, DayOfWeek dayOfWeek) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<GroupTimeSlotPattern> patterns = groupTimeSlotPatternRepository.findByTeacherAndDayOfWeek(teacher, dayOfWeek);
        log.info("Find pattens: {}", patterns.size());
        return patterns;
    }

    private void checkTeacherLesson(Teacher teacher, GroupLesson lesson) {
        Set<UUID> lessonIds = teacher.getGroupLessons().stream()
                .map(GroupLesson::getId)
                .collect(Collectors.toSet());

        if (!lessonIds.contains(lesson.getId())) {
            throw new IllegalArgumentException("Lesson not for the teacher: " + teacher);
        }
    }


}
