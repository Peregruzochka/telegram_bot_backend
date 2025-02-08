package ru.peregruzochka.telegram_bot_backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.RegistrationEvent;
import ru.peregruzochka.telegram_bot_backend.mapper.RegistrationEventMapper;
import ru.peregruzochka.telegram_bot_backend.model.Registration;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewRegistrationEventPublisher {

    private final ObjectMapper objectMapper;
    private final RegistrationEventMapper registrationEventMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.new-registration}")
    private String channel;

    public void publish(Registration registration) {
        RegistrationEvent registrationEvent = registrationEventMapper.toRegistrationEvent(registration);
        try {
            String event = objectMapper.writeValueAsString(registrationEvent);
            log.info("New registration event: {}", event);
            redisTemplate.convertAndSend(channel, event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
