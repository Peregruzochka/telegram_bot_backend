package ru.peregruzochka.telegram_bot_backend.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.service.CancelService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCancelRegistrationListener implements MessageListener {

    private final CancelService cancelService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received a message on a local cancel registration {}", message.getBody());

        UUID registrationId = UUID.fromString(new String(message.getBody()));
        Registration registration = Registration.builder().id(registrationId).build();
        Cancel cancel = Cancel.builder()
                .registration(registration)
                .caseDescription("after-confirm-question")
                .build();

        cancelService.addCancel(cancel);
    }
}
