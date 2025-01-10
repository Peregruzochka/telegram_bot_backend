package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.repository.LessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        log.info("Get all lessons. Found: {}", lessons);
        return lessons;
    }

    @Transactional
    public Lesson addLesson(@RequestBody Lesson newLesson) {
        Lesson lesson = lessonRepository.save(newLesson);
        log.info("Add lesson: {}", lesson);
        return lesson;
    }

    @Transactional
    public Lesson addLessonToTeacher(UUID lessonId, UUID teacherId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new IllegalArgumentException("Lesson not found")
        );

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<Teacher> teachers = lesson.getTeachers();
        if (teachers == null) {
            teachers = new ArrayList<>();
            lesson.setTeachers(teachers);
        }
        lesson.getTeachers().add(teacher);

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Lesson {} added to teacher {}", savedLesson, teacher);
        return savedLesson;
    }
}
