package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupLessonDto;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupLessonMapper {

    private final TeacherMapper teacherMapper;

    public GroupLesson mapToGroupLessonEntity(GroupLessonDto groupLessonDto) {
        return GroupLesson.builder()
                .id(groupLessonDto.getId())
                .groupSize(groupLessonDto.getGroupSize())
                .name(groupLessonDto.getName())
                .description(groupLessonDto.getDescription())
                .build();
    }

    public GroupLessonDto mapToGroupLessonDto(GroupLesson groupLesson) {
        return GroupLessonDto.builder()
                .id(groupLesson.getId())
                .groupSize(groupLesson.getGroupSize())
                .name(groupLesson.getName())
                .description(groupLesson.getDescription())
                .teachers(teacherMapper.toTeacherDtoList(groupLesson.getTeachers()))
                .build();
    }

    public List<GroupLessonDto> mapToGroupLessonDtoList(List<GroupLesson> groupLessons) {
        if (groupLessons == null || groupLessons.isEmpty()) {
            return null;
        } else {
            return groupLessons.stream()
                    .map(this::mapToGroupLessonDto)
                    .toList();
        }
    }
}
