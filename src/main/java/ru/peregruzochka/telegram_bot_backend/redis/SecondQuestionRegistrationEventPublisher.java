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

@Component
@Slf4j
@RequiredArgsConstructor
public class SecondQuestionRegistrationEventPublisher {
    private final RegistrationEventMapper registrationEventMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.second-question}")
    private String channel;

    public void publish(Registration registration) {
        Long telegramId = registration.getUser().getTelegramId();
        RegistrationEvent registrationEvent = registrationEventMapper.toRegistrationEventWithTgId(registration, telegramId);
        try {
            String message = objectMapper.writeValueAsString(registrationEvent);
            redisTemplate.convertAndSend(channel, message);
            log.info("Second question registration event published: {}", message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
