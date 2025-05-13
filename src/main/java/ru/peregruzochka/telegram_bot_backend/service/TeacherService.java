package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.Image;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.repository.GroupLessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.ImageRepository;
import ru.peregruzochka.telegram_bot_backend.repository.LessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {
    private final ImageRepository imageRepository;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final GroupLessonRepository groupLessonRepository;

    @Transactional
    public Teacher addTeacher(String name, Image image) {
        Image savedImage = imageRepository.save(image);
        Teacher teacher = Teacher.builder()
                .name(name)
                .image(savedImage)
                .hidden(false)
                .build();
        Teacher savedTeacher = teacherRepository.save(teacher);
        log.info("Teacher added: {}", savedTeacher);
        return savedTeacher;
    }

    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        log.info("Teachers found: {}", teachers.size());
        return teachers;
    }

    @Transactional(readOnly = true)
    public List<Teacher> getGroupTeachers() {
        List<Teacher> teachers = teacherRepository.findTeachersByAllGroupLessons();
        log.info("Teachers with group lessons found: {}", teachers.size());
        return teachers;
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachersByLessonId(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new IllegalArgumentException("Lesson not found: " + lessonId)
        );

        List<Teacher> teachers = teacherRepository.findTeachersByLesson(lesson);
        log.info("Teachers with lesson {} found: {}", lessonId, teachers.size());
        return teachers;
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachersByGroupLessonId(UUID lessonId) {
        GroupLesson groupLesson = groupLessonRepository.findById(lessonId).orElseThrow(
                () -> new IllegalArgumentException("Group lesson not found: " + lessonId)
        );

        List<Teacher> teachers = teacherRepository.findTeachersByGroupLesson(groupLesson);
        log.info("Teachers with group lesson {} found: {}", lessonId, teachers.size());
        return teachers;
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachersBySlotDate(LocalDate slotDate) {
        LocalDateTime from = slotDate.atStartOfDay();
        LocalDateTime to = from.plusDays(1);
        Set<Teacher> teachers = teacherRepository.findByTimeSlotDate(from, to);
        Set<Teacher> groupTeachers = teacherRepository.findByGroupTimeSlotDate(from, to);
        teachers.containsAll(groupTeachers);
        log.info("Teachers by slot date [{}] found: {}", slotDate, teachers.size());
        return teachers.stream().toList();
    }
}
