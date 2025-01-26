package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.LessonDto;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LessonMapper {
    private final TeacherMapper teacherMapper;

    public LessonDto toLessonDto(Lesson lesson) {
        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .teachers(teacherMapper.toTeacherDtoList(lesson.getTeachers()))
                .build();
    }

    public Lesson toLessonEntity(LessonDto lessonDto) {
        return Lesson.builder()
                .id(lessonDto.getId())
                .name(lessonDto.getName())
                .description(lessonDto.getDescription())
                .build();
    }

    public List<LessonDto> toLessonDtoList(List<Lesson> lessons) {
        return lessons.stream()
                .map(this::toLessonDto)
                .collect(Collectors.toList());
    }
}
