package ru.peregruzochka.telegram_bot_backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCancelPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis-channel.local-cancel}")
    private String channel;

    public void publish(LocalCancelEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);
            log.info("Published local cancel {}", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
