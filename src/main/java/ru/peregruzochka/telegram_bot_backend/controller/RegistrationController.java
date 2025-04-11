package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.CreateAtRegistrationDto;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto;
import ru.peregruzochka.telegram_bot_backend.mapper.CreatedAtMapper;
import ru.peregruzochka.telegram_bot_backend.mapper.RegistrationMapper;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.service.RegistrationService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;
    private final CreatedAtMapper createdAtMapper;

    @PostMapping
    public RegistrationDto addRegistration(@RequestBody RegistrationDto registrationDto) {
        Registration registration = registrationMapper.toRegistrationEntity(registrationDto);
        Registration savedRegistration = registrationService.addRegistration(registration);
        return registrationMapper.toRegistrationDto(savedRegistration);
    }

    @GetMapping("/actual")
    public List<RegistrationDto> getAllUserActualRegistrations(@RequestParam("user-id") UUID userId) {
        List<Registration> registrations = registrationService.getAllActualRegistration(userId);
        return registrationMapper.toRegistrationDtoList(registrations);
    }

    @PostMapping("/update")
    public RegistrationDto updateRegistration(@RequestBody RegistrationDto registrationDto) {
        Registration registration = registrationMapper.toRegistrationEntity(registrationDto);
        Registration updatedRegistration = registrationService.updateRegistration(registration);
        return registrationMapper.toRegistrationDto(updatedRegistration);
    }

    @GetMapping("/search-today")
    public List<RegistrationDto> getAllRegistrationsByToday() {
        List<Registration> registrations = registrationService.getAllRegistrationByToday();
        return registrationMapper.toRegistrationDtoList(registrations);
    }

    @GetMapping("/search-actual-by-date")
    public List<RegistrationDto> getAllActualRegistrationsByToday(@RequestParam("date") LocalDate date) {
        List<Registration> registrations = registrationService.getAllActualRegistrationByDate(date);
        return registrationMapper.toRegistrationDtoList(registrations);
    }

    @GetMapping("/search-actual-by-teacher-by-date")
    public List<RegistrationDto> getAllActualRegistrationsByTeacherByDate(@RequestParam("teacher-id") UUID teacherId,
                                                                          @RequestParam("date") LocalDate date) {
        List<Registration> registrations = registrationService.getAllActualRegistrationByTeacherByDate(teacherId, date);
        return registrationMapper.toRegistrationDtoList(registrations);
    }

    @GetMapping("/search-by-date")
    public List<RegistrationDto> getAllRegistrationsByDate(@RequestParam("date") LocalDate date) {
        List<Registration> registrations = registrationService.getAllRegistrationByDate(date);
        return registrationMapper.toRegistrationDtoList(registrations);
    }


    @PutMapping("/{registration-id}/confirm")
    public RegistrationDto confirmRegistration(@PathVariable("registration-id") UUID registrationId) {
        Registration registration = registrationService.confirm(registrationId);
        return registrationMapper.toRegistrationDto(registration);
    }

    @PutMapping("/{registration-id}/decline")
    public RegistrationDto declineRegistration(@PathVariable("registration-id") UUID registrationId) {
        Registration registration = registrationService.decline(registrationId);
        return registrationMapper.toRegistrationDto(registration);
    }

    @GetMapping("/{id}")
    public RegistrationDto getRegistration(@PathVariable("id") UUID id) {
        Registration registration = registrationService.getRegistrationById(id);
        return registrationMapper.toRegistrationDto(registration);
    }

    @GetMapping("/{id}/created-at")
    public CreateAtRegistrationDto getCreatedAt(@PathVariable("id") UUID id) {
        Registration registration = registrationService.getRegistrationById(id);
        return createdAtMapper.toCreateAtRegistrationDto(registration);
    }
}
