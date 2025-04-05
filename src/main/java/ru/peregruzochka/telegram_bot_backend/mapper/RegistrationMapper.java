package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto;
import ru.peregruzochka.telegram_bot_backend.dto.TeacherDto;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrationMapper {
    private final ChildMapper childMapper;
    private final LessonMapper lessonMapper;
    private final UserMapper userMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final TeacherMapper teacherMapper;

    public Registration toRegistrationEntity(RegistrationDto registrationDto) {
        return Registration.builder()
                .id(registrationDto.getId())
                .child(childMapper.toChildEntity(registrationDto.getChild()))
                .user(userMapper.toUserEntity(registrationDto.getUser()))
                .lesson(lessonMapper.toLessonEntity(registrationDto.getLesson()))
                .timeslot(timeSlotMapper.toTimeSlotEntity(registrationDto.getSlot()))
                .build();
    }

    public RegistrationDto toRegistrationDto(Registration registration) {
        return RegistrationDto.builder()
                .id(registration.getId())
                .child(childMapper.toChildDto(registration.getChild()))
                .user(userMapper.toUserDto(registration.getUser()))
                .lesson(lessonMapper.toLessonDto(registration.getLesson()))
                .teacher(getTeacher(registration.getTimeslot()))
                .slot(timeSlotMapper.toTimeSlotDto(registration.getTimeslot()))
                .build();
    }

    public List<RegistrationDto> toRegistrationDtoList(List<Registration> registrations) {
        return registrations.stream()
                .map(this::toRegistrationDto)
                .collect(Collectors.toList());
    }

    private TeacherDto getTeacher(TimeSlot timeSlot) {
        if (timeSlot != null && timeSlot.getTeacher() != null) {
            return teacherMapper.toTeacherDto(timeSlot.getTeacher());
        } else {
            return null;
        }
    }
}
