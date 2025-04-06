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

@Slf4j
@Component
@RequiredArgsConstructor
public class NewGroupRegistrationEventPublisher {

    private final ObjectMapper objectMapper;
    private final GroupRegistrationEventMapper registrationEventMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.new-group-registration}")
    private String channel;

    public void publish(GroupRegistration registration) {
        GroupRegistrationEvent registrationEvent = registrationEventMapper.map(registration);
        try {
            String event = objectMapper.writeValueAsString(registrationEvent);
            log.info("New group registration event: {}", event);
            redisTemplate.convertAndSend(channel, event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
