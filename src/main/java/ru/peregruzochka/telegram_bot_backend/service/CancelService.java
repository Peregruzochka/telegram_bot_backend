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
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;

import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto.RegistrationType.CANCEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelService {
    private final RegistrationRepository registrationRepository;
    private final CancelRepository cancelRepository;
    private final TimeSlotRepository timeSlotRepository;


    @Transactional
    public Cancel addCancel(Cancel cancel) {
        UUID registrationId = cancel.getRegistration().getId();

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (registration.getType().equals(CANCEL)) {
            throw new IllegalArgumentException("Can't cancel registration");
        }

        TimeSlot timeSlot = registration.getTimeslot();
        timeSlot.setIsAvailable(true);
        timeSlotRepository.save(timeSlot);

        registration.setType(CANCEL);
        registration.setTimeslot(null);
        registrationRepository.save(registration);

        cancel.setRegistration(registration);
        cancel.setStartTime(timeSlot.getStartTime());

        Cancel savedCancel = cancelRepository.save(cancel);
        log.info("Saved cancel: {}", savedCancel);
        return savedCancel;
    }
}
