package ru.peregruzochka.telegram_bot_backend.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCancelPublisher {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.local-cancel}")
    private String channel;

    public void publish(UUID registrationId) {
        redisTemplate.convertAndSend(channel, registrationId.toString());
        log.info("Published registrationId to cancel {}", registrationId);
    }
}
