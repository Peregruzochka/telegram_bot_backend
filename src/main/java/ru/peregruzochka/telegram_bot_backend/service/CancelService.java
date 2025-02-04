package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.repository.CancelRepository;
import ru.peregruzochka.telegram_bot_backend.repository.RegistrationRepository;

import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto.RegistrationType.CANCEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelService {
    private final RegistrationRepository registrationRepository;
    private final CancelRepository cancelRepository;


    @Transactional
    public Cancel addCancel(Cancel cancel) {
        UUID registrationId = cancel.getRegistration().getId();

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (registration.getType().equals(CANCEL)) {
            throw new IllegalArgumentException("Can't cancel registration");
        }

        registration.setType(CANCEL);
        cancel.setRegistration(registration);
        TimeSlot timeSlot = registration.getTimeslot();
        timeSlot.setIsAvailable(true);

        Cancel savedCancel = cancelRepository.save(cancel);
        log.info("Saved cancel: {}", savedCancel);
        return savedCancel;
    }
}
