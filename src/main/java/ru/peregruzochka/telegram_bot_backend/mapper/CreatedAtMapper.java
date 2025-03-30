package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.CreateAtRegistrationDto;
import ru.peregruzochka.telegram_bot_backend.model.Registration;

@Component
public class CreatedAtMapper {

   public CreateAtRegistrationDto toCreateAtRegistrationDto(Registration registration) {
      return CreateAtRegistrationDto.builder()
              .registrationId(registration.getId())
              .createdAt(registration.getCreatedAt())
              .build();
   }
}
