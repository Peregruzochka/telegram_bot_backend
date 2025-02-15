package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationEvent;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;

@Component
public class RegistrationEventMapper {
    public RegistrationEvent toRegistrationEvent(Registration registration) {
        return RegistrationEvent.builder()
                .registrationId(registration.getId())
                .telegramId(0L)
                .userName(registration.getUser().getUserName())
                .childName(registration.getChild().getChildName())
                .childrenBirthday(registration.getChild().getBirthday())
                .teacherName(registration.getTimeslot().getTeacher().getName())
                .lessonName(registration.getLesson().getName())
                .registrationType(registration.getType().toString())
                .startTime(registration.getTimeslot().getStartTime())
                .endTime(registration.getTimeslot().getEndTime())
                .build();
    }

    public RegistrationEvent toRegistrationEvent(Registration registration, TimeSlot timeSlot) {
        return RegistrationEvent.builder()
                .registrationId(registration.getId())
                .telegramId(0L)
                .userName(registration.getUser().getUserName())
                .childName(registration.getChild().getChildName())
                .childrenBirthday(registration.getChild().getBirthday())
                .teacherName(timeSlot.getTeacher().getName())
                .lessonName(registration.getLesson().getName())
                .registrationType(registration.getType().toString())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .build();
    }

    public RegistrationEvent toRegistrationEventWithTgId(Registration registration, Long telegramId) {
        return RegistrationEvent.builder()
                .registrationId(registration.getId())
                .telegramId(telegramId)
                .userName(registration.getUser().getUserName())
                .childName(registration.getChild().getChildName())
                .childrenBirthday(registration.getChild().getBirthday())
                .teacherName(registration.getTimeslot().getTeacher().getName())
                .lessonName(registration.getLesson().getName())
                .registrationType(registration.getType().toString())
                .startTime(registration.getTimeslot().getStartTime())
                .endTime(registration.getTimeslot().getEndTime())
                .build();
    }
}
