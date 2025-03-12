package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.LessonDto;
import ru.peregruzochka.telegram_bot_backend.mapper.LessonMapper;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.service.LessonService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lessons")
public class LessonController {
    private final LessonMapper lessonMapper;
    private final LessonService lessonService;

    @GetMapping("/all")
    public List<LessonDto> getAllLessons() {
        List<Lesson> lessons = lessonService.getAllLessons();
        log.info(LocalDateTime.now().toString());
        return lessonMapper.toLessonDtoList(lessons);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonDto addLesson(@RequestBody LessonDto lessonDto) {
        Lesson newLesson = lessonMapper.toLessonEntity(lessonDto);
        Lesson savedLesson = lessonService.addLesson(newLesson);
        return lessonMapper.toLessonDto(savedLesson);
    }

    @PutMapping("/{lesson-id}/add-teacher")
    public LessonDto addLessonToTeacher(@PathVariable(name = "lesson-id") UUID lessonId,
                                        @RequestParam(name = "teacher-id") UUID teacherId) {
        Lesson lesson = lessonService.addLessonToTeacher(lessonId, teacherId);
        return lessonMapper.toLessonDto(lesson);
    }
}
