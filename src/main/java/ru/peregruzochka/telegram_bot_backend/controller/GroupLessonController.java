package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.GroupLessonDto;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupLessonMapper;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.service.GroupLessonService;

import java.util.UUID;

@RestController
@RequestMapping("/group-lessons")
@RequiredArgsConstructor
public class GroupLessonController {

    private final GroupLessonMapper groupLessonMapper;
    private final GroupLessonService groupLessonService;

    @PostMapping
    public GroupLessonDto addGroupLesson(@RequestBody GroupLessonDto groupLessonDto) {
        GroupLesson newGroupLesson = groupLessonMapper.mapToGroupLessonEntity(groupLessonDto);
        GroupLesson groupLesson = groupLessonService.addGroupLesson(newGroupLesson);
        return groupLessonMapper.mapToGroupLessonDto(groupLesson);
    }

    @PutMapping("/{group-lesson-id}/add-teacher")
    public GroupLessonDto addLessonToTeacher(@PathVariable(name = "group-lesson-id") UUID lessonId,
                                             @RequestParam(name = "teacher-id") UUID teacherId) {
        GroupLesson groupLesson = groupLessonService.addLessonToTeacher(lessonId, teacherId);
        return groupLessonMapper.mapToGroupLessonDto(groupLesson);
    }
}
