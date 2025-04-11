package ru.peregruzochka.telegram_bot_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.redis.FirstQuestionGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.FirstQuestionRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.NotConfirmedGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.NotConfirmedRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.QRSenderGroupPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.QRSenderPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.SecondQuestionGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.SecondQuestionRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.service.GroupRegistrationService;
import ru.peregruzochka.telegram_bot_backend.service.RegistrationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConfirmScheduler {

    private final RegistrationService registrationService;
    private final NotConfirmedRegistrationEventPublisher notConfirmedRegistrationEventPublisher;
    private final FirstQuestionRegistrationEventPublisher firstQuestionRegistrationEventPublisher;
    private final SecondQuestionRegistrationEventPublisher secondQuestionRegistrationEventPublisher;
    private final QRSenderPublisher qrSenderPublisher;
    private final QRSenderGroupPublisher qrSenderGroupPublisher;
    private final GroupRegistrationService groupRegistrationService;
    private final NotConfirmedGroupRegistrationEventPublisher notConfirmedGroupRegistrationEventPublisher;
    private final FirstQuestionGroupRegistrationEventPublisher firstQuestionGroupRegistrationEventPublisher;
    private final SecondQuestionGroupRegistrationEventPublisher secondQuestionGroupRegistrationEventPublisher;

    @Scheduled(cron = "${scheduler.get-non-confirmed}")
    public void schedule() {
        List<Registration> registrations = registrationService.getNotConfirmed();
        registrations.forEach(notConfirmedRegistrationEventPublisher::publish);
        List<GroupRegistration> groupRegistrations = groupRegistrationService.getNotConfirmed();
        groupRegistrations.forEach(notConfirmedGroupRegistrationEventPublisher::publish);

        List<Registration> registrationsTwo = registrationService.getFirstQuestionRegistration();
        registrationsTwo.forEach(firstQuestionRegistrationEventPublisher::publish);
        List<GroupRegistration> groupRegistrationsTwo = groupRegistrationService.getFirstQuestionRegistration();
        groupRegistrationsTwo.forEach(firstQuestionGroupRegistrationEventPublisher::publish);

        List<Registration> registrationsThree = registrationService.getSecondQuestionRegistration();
        registrationsThree.forEach(secondQuestionRegistrationEventPublisher::publish);
        List<GroupRegistration> groupRegistrationsThree = groupRegistrationService.getSecondQuestionRegistration();
        groupRegistrationsThree.forEach(secondQuestionGroupRegistrationEventPublisher::publish);

        List<Registration> registrationFour = registrationService.getAutoConfirmedRegistration();
        registrationFour.forEach(qrSenderPublisher::publish);
        List<GroupRegistration> groupRegistrationsFour = groupRegistrationService.getAutoConfirmedRegistration();
        groupRegistrationsFour.forEach(qrSenderGroupPublisher::publish);
    }
}
