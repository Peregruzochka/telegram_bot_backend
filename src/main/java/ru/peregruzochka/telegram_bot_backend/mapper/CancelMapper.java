package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.CancelDto;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.model.Registration;

@Component
public class CancelMapper {

    public Cancel toCancelEntity(CancelDto cancelDto) {
        return Cancel.builder()
                .id(cancelDto.getId())
                .registration(Registration.builder().id(cancelDto.getRegistrationId()).build())
                .caseDescription(cancelDto.getCaseDescription())
                .build();
    }

    public CancelDto toCancelDto(Cancel cancel) {
        return CancelDto.builder()
                .id(cancel.getId())
                .registrationId(cancel.getRegistration().getId())
                .caseDescription(cancel.getCaseDescription())
                .build();
    }
}
