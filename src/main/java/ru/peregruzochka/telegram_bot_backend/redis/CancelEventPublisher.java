package ru.peregruzochka.telegram_bot_backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.CancelEvent;
import ru.peregruzochka.telegram_bot_backend.mapper.CancelEventMapper;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelEventPublisher {

    private final CancelEventMapper cancelEventMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.cancel}")
    private String channel;

    public void publish(Cancel cancel, TimeSlot timeSlot) {
        CancelEvent event = cancelEventMapper.mapToCancelEvent(cancel, timeSlot);
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);
            log.info("Cancel event published: {}", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
