package ru.peregruzochka.telegram_bot_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.service.RegistrationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConfirmScheduler {

    private final RegistrationService registrationService;

    @Scheduled(cron = "${scheduler.get-non-confirmed}")
    public void schedule() {
        List<Registration> registrations = registrationService.getNotConfirmed();
    }
}
