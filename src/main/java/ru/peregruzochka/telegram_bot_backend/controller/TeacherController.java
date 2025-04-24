package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.peregruzochka.telegram_bot_backend.dto.TeacherDto;
import ru.peregruzochka.telegram_bot_backend.mapper.ImageMapper;
import ru.peregruzochka.telegram_bot_backend.mapper.TeacherMapper;
import ru.peregruzochka.telegram_bot_backend.model.Image;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.service.TeacherService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/teachers")
public class TeacherController {
    private final TeacherService teacherService;
    private final TeacherMapper teacherMapper;
    private final ImageMapper imageMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDto addTeacher(@RequestParam MultipartFile file,
                                 @RequestParam String name) {
        Image image = imageMapper.toImageEntity(file);
        Teacher teacher = teacherService.addTeacher(name, image);
        return teacherMapper.toTeacherDto(teacher);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherDto> getAllTeachers() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        return teacherMapper.toTeacherDtoList(teachers);
    }

    @GetMapping("/group-teachers")
    public List<TeacherDto> getGroupTeacher() {
        List<Teacher> teachers = teacherService.getGroupTeachers();
        return teacherMapper.toTeacherDtoList(teachers);
    }

    @GetMapping("/by-lesson")
    public List<TeacherDto> getTeachersByLesson(@RequestParam UUID lessonId) {
        List<Teacher> teachers = teacherService.getTeachersByLessonId(lessonId);
        return teacherMapper.toTeacherDtoList(teachers);
    }

    @GetMapping("/by-group-lesson")
    public List<TeacherDto> getTeachersByGroupLesson(@RequestParam UUID lessonId) {
        List<Teacher> teachers = teacherService.getTeachersByGroupLessonId(lessonId);
        return teacherMapper.toTeacherDtoList(teachers);
    }
}
