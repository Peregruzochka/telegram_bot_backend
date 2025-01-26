package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto;
import ru.peregruzochka.telegram_bot_backend.mapper.RegistrationMapper;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.service.RegistrationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;

    @PostMapping
    public RegistrationDto addRegistration(@RequestBody RegistrationDto registrationDto) {
        Registration registration = registrationMapper.toRegistrationEntity(registrationDto);
        Registration savedRegistration = registrationService.addRegistration(registration);
        return registrationMapper.toRegistrationDto(savedRegistration);
    }
}
