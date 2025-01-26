package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto;
import ru.peregruzochka.telegram_bot_backend.mapper.RegistrationMapper;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.service.RegistrationService;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public List<RegistrationDto> getAllUserRegistrations(@RequestParam("user-id") UUID userId) {
        List<Registration> registrations = registrationService.getAllUserRegistration(userId);
        return registrationMapper.toRegistrationDtoList(registrations);
    }
}
