package ru.peregruzochka.telegram_bot_backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupRegistrationEvent;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupRegistrationEventMapper;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;

@Component
@Slf4j
@RequiredArgsConstructor
public class FirstQuestionGroupRegistrationEventPublisher {

    private final GroupRegistrationEventMapper groupRegistrationEventMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.group-first-question}")
    private String channel;

    public void publish(GroupRegistration registration) {
        GroupRegistrationEvent registrationEvent = groupRegistrationEventMapper.map(registration);
        try {
            String message = objectMapper.writeValueAsString(registrationEvent);
            redisTemplate.convertAndSend(channel, message);
            log.info("First question group registration event published: {}", message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
