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
import ru.peregruzochka.telegram_bot_backend.service.GroupCancelService;

import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent.CancelType.GROUP;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCancelRegistrationListener implements MessageListener {

    private final CancelService cancelService;
    private final ObjectMapper objectMapper;
    private final GroupCancelService groupCancelService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received a message on a local cancel registration {}", message.getBody());
        String event =  new String(message.getBody());
        try {
            LocalCancelEvent cancelEvent = objectMapper.readValue(event, LocalCancelEvent.class);

            if (cancelEvent.getType() == LocalCancelEvent.CancelType.INDIVIDUAL) {
                Registration registration = Registration.builder()
                        .id(cancelEvent.getRegistrationId())
                        .build();

                Cancel cancel = Cancel.builder()
                        .registration(registration)
                        .caseDescription(cancelEvent.getCaseDescription())
                        .build();

                cancelService.addCancel(cancel);
            } else if (cancelEvent.getType() == GROUP) {
                UUID registrationId = cancelEvent.getRegistrationId();
                String caseDescription = cancelEvent.getCaseDescription();
                groupCancelService.addGroupCancel(registrationId, caseDescription);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
