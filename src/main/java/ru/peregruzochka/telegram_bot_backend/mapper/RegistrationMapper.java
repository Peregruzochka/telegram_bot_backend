package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto;
import ru.peregruzochka.telegram_bot_backend.model.Registration;

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
                .type(registrationDto.getType())
                .build();
    }

    public RegistrationDto toRegistrationDto(Registration registration) {
        return RegistrationDto.builder()
                .id(registration.getId())
                .telegramId(registration.getUser().getTelegramId())

                .child(childMapper.toChildDto(registration.getChild()))
                .user(userMapper.toUserDto(registration.getUser()))
                .lesson(lessonMapper.toLessonDto(registration.getLesson()))
                .teacher(teacherMapper.toTeacherDto(registration.getTimeslot().getTeacher()))
                .slot(timeSlotMapper.toTimeSlotDto(registration.getTimeslot()))
                .build();
    }
}
