package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.repository.GroupLessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupLessonService {

    private final GroupLessonRepository groupLessonRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public GroupLesson addGroupLesson(GroupLesson newGroupLesson) {
        GroupLesson groupLesson = groupLessonRepository.save(newGroupLesson);
        log.info("New group lesson: {}", groupLesson);
        return groupLesson;
    }

    @Transactional
    public GroupLesson addLessonToTeacher(UUID lessonId, UUID teacherId) {
        GroupLesson groupLesson = groupLessonRepository.findById(lessonId).orElseThrow(
                () -> new IllegalArgumentException("Group lesson not found")
        );

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<Teacher> teachers = groupLesson.getTeachers();
        if (teachers == null) {
            teachers = new ArrayList<>();
            groupLesson.setTeachers(teachers);
        }
        groupLesson.getTeachers().add(teacher);

        GroupLesson savedGroupLesson = groupLessonRepository.save(groupLesson);
        log.info("Add teacher to group lesson: {}", savedGroupLesson);
        return savedGroupLesson;
    }

    @Transactional(readOnly = true)
    public List<GroupLesson> getGroupLessonsByTeacher(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<GroupLesson> groupLessons = groupLessonRepository.findAllByTeacher(teacher);
        log.info("Get group lessons by teacher: {}", groupLessons.size());
        return groupLessons;
    }
}
