package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.TeacherDto;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeacherMapper {
    public TeacherDto toTeacherDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .imageID(teacher.getImage().getId())
                .hidden(teacher.isHidden())
                .build();
    }

    public List<TeacherDto> toTeacherDtoList(List<Teacher> teachers) {
        if (teachers == null) {
            return null;
        }
        return teachers.stream()
                .map(this::toTeacherDto)
                .collect(Collectors.toList());
    }

    public Teacher toTeacherEntity(TeacherDto teacherDto) {
        return Teacher.builder()
                .id(teacherDto.getId())
                .name(teacherDto.getName())
                .build();
    }
}
