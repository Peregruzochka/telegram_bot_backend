package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupRegistrationEvent;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;

@Component
@RequiredArgsConstructor
public class GroupRegistrationEventMapper {

    public GroupRegistrationEvent map(GroupRegistration groupRegistration) {
        return GroupRegistrationEvent.builder()
                .telegramId(groupRegistration.getUser().getTelegramId())
                .registrationId(groupRegistration.getId())
                .username(groupRegistration.getUser().getUserName())
                .phoneNumber(groupRegistration.getUser().getPhone())
                .userStatus(groupRegistration.getUser().getStatus().toString())
                .childName(groupRegistration.getChild().getChildName())
                .childBirthday(groupRegistration.getChild().getBirthday())
                .teacherName(groupRegistration.getGroupTimeslot().getTeacher().getName())
                .lessonName(groupRegistration.getGroupTimeslot().getGroupLesson().getName())
                .startTime(groupRegistration.getGroupTimeslot().getStartTime())
                .endTime(groupRegistration.getGroupTimeslot().getEndTime())
                .capacity(groupRegistration.getGroupTimeslot().getGroupLesson().getGroupSize())
                .amount(groupRegistration.getGroupTimeslot().getRegistrations().size())
                .build();
    }

    public GroupRegistrationEvent map(GroupRegistration groupRegistration, GroupTimeSlot groupTimeSlot) {
        return GroupRegistrationEvent.builder()
                .username(groupRegistration.getUser().getUserName())
                .userStatus(groupRegistration.getUser().getStatus().toString())
                .phoneNumber(groupRegistration.getUser().getPhone())
                .childName(groupRegistration.getChild().getChildName())
                .childBirthday(groupRegistration.getChild().getBirthday())
                .teacherName(groupTimeSlot.getTeacher().getName())
                .lessonName(groupTimeSlot.getGroupLesson().getName())
                .startTime(groupTimeSlot.getStartTime())
                .endTime(groupTimeSlot.getEndTime())
                .capacity(groupTimeSlot.getGroupLesson().getGroupSize())
                .amount(groupTimeSlot.getRegistrations().size())
                .build();
    }
}
