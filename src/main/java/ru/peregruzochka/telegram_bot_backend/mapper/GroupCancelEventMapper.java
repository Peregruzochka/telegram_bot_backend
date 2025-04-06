package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupCancelEvent;
import ru.peregruzochka.telegram_bot_backend.model.GroupCancel;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;

@Component
@RequiredArgsConstructor
public class GroupCancelEventMapper {

    private final GroupRegistrationEventMapper groupRegistrationEventMapper;

    public GroupCancelEvent map(GroupCancel cancel, GroupTimeSlot timeSlot) {
        return GroupCancelEvent.builder()
                .groupRegistrationEvent(groupRegistrationEventMapper.map(cancel.getGroupRegistration(), timeSlot))
                .caseDescription(cancel.getCaseDescription())
                .build();
    }

}
