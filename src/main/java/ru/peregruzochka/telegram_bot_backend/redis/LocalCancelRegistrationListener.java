package ru.peregruzochka.telegram_bot_backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.service.CancelService;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCancelRegistrationListener implements MessageListener {

    private final CancelService cancelService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received a message on a local cancel registration {}", message.getBody());
        String event =  new String(message.getBody());
        try {
            LocalCancelEvent cancelEvent = objectMapper.readValue(event, LocalCancelEvent.class);

            Registration registration = Registration.builder()
                    .id(cancelEvent.getRegistrationId())
                    .build();

            Cancel cancel = Cancel.builder()
                    .registration(registration)
                    .caseDescription(cancelEvent.getCaseDescription())
                    .build();

            cancelService.addCancel(cancel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
