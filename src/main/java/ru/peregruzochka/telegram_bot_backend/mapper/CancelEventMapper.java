package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.CancelEvent;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;

@Component
@RequiredArgsConstructor
public class CancelEventMapper {
    private final RegistrationEventMapper registrationEventMapper;

    public CancelEvent mapToCancelEvent(Cancel cancel, TimeSlot timeSlot) {
        return CancelEvent.builder()
                .registrationEvent(registrationEventMapper.toRegistrationEvent(cancel.getRegistration(), timeSlot))
                .caseDescription(cancel.getCaseDescription())
                .build();
    }
}
