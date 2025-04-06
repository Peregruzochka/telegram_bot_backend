package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.GroupRegistrationDto;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupRegistrationMapper;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.service.GroupRegistrationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/group-registrations")
@RequiredArgsConstructor
public class GroupRegistrationController {

    private final GroupRegistrationMapper groupRegistrationMapper;
    private final GroupRegistrationService groupRegistrationService;

    @PostMapping
    public GroupRegistrationDto addGroupRegistration(@RequestBody GroupRegistrationDto groupRegistrationDto) {
        GroupRegistration groupRegistration = groupRegistrationMapper.toGroupRegistrationEntity(groupRegistrationDto);
        GroupRegistration newGroupRegistration = groupRegistrationService.addGroupRegistration(groupRegistration);
        return groupRegistrationMapper.toGroupRegistrationDto(newGroupRegistration);
    }

    @GetMapping("/actual")
    public List<GroupRegistrationDto> getGroupRegistrations(@RequestParam("user-id") UUID userId) {
        List<GroupRegistration> registrations = groupRegistrationService.getAllActualRegistration(userId);
        return groupRegistrationMapper.toGroupRegistrationDtoList(registrations);
    }

    @GetMapping("/{group-registration-id}")
    public GroupRegistrationDto getGroupRegistration(@PathVariable("group-registration-id") UUID groupRegistrationId) {
        GroupRegistration registration = groupRegistrationService.getRegistrationById(groupRegistrationId);
        return groupRegistrationMapper.toGroupRegistrationDto(registration);
    }
}
