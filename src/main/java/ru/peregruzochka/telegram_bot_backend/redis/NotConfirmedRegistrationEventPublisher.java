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
public class NotConfirmedRegistrationEventPublisher {

    private final RegistrationEventMapper registrationEventMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.not-confirmed}")
    private String channel;

    public void publish(Registration registration) {
        try {
            Long telegramId = registration.getUser().getTelegramId();
            RegistrationEvent registrationEvent = registrationEventMapper.toRegistrationEventWithTgId(registration, telegramId);
            String message = objectMapper.writeValueAsString(registrationEvent);
            log.info("Not-confirmed event published: {}", message);
            redisTemplate.convertAndSend(channel, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
