package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

import java.time.LocalDate;
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


    @PutMapping("/{teacher-id}/update-photo")
    @ResponseStatus(HttpStatus.OK)
    public TeacherDto updateTeacherPhoto(@PathVariable("teacher-id") UUID teacherId,
                                         @RequestParam MultipartFile file) {
        Image image = imageMapper.toImageEntity(file);
        Teacher teacher = teacherService.updateTeacherPhoto(teacherId, image);
        return teacherMapper.toTeacherDto(teacher);
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

    @GetMapping("/by-slot-date")
    public List<TeacherDto> getTeachersBySlotDate(@RequestParam LocalDate slotDate) {
        List<Teacher> teachers = teacherService.getTeachersBySlotDate(slotDate);
        return teacherMapper.toTeacherDtoList(teachers);
    }

    @GetMapping("/{id}")
    public TeacherDto getTeacherById(@PathVariable UUID id) {
        Teacher teacher = teacherService.getById(id);
        return teacherMapper.toTeacherDto(teacher);
    }
}
