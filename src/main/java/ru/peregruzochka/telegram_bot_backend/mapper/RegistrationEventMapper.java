package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationEvent;
import ru.peregruzochka.telegram_bot_backend.model.Registration;

@Component
public class RegistrationEventMapper {
    public RegistrationEvent toRegistrationEvent(Registration registration) {
        return RegistrationEvent.builder()
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
