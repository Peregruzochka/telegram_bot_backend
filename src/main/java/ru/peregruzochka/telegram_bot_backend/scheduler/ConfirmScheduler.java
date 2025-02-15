package ru.peregruzochka.telegram_bot_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.redis.FirstQuestionRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.NotConfirmedRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.service.RegistrationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConfirmScheduler {

    private final RegistrationService registrationService;
    private final NotConfirmedRegistrationEventPublisher notConfirmedRegistrationEventPublisher;
    private final FirstQuestionRegistrationEventPublisher firstQuestionRegistrationEventPublisher;

    @Scheduled(cron = "${scheduler.get-non-confirmed}")
    public void schedule() {
        List<Registration> registrations = registrationService.getNotConfirmed();
        registrations.forEach(notConfirmedRegistrationEventPublisher::publish);

        List<Registration> registrationsTwo = registrationService.getFirstQuestionRegistration();
        registrationsTwo.forEach(firstQuestionRegistrationEventPublisher::publish);
    }
}
