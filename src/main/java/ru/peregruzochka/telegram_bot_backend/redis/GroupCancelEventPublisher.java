package ru.peregruzochka.telegram_bot_backend.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupCancelEvent;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupCancelEventMapper;
import ru.peregruzochka.telegram_bot_backend.model.GroupCancel;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupCancelEventPublisher {

    private final GroupCancelEventMapper groupCancelEventMapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis-channel.group-cancel}")
    private String channel;

    public void publish(GroupCancel groupCancel, GroupTimeSlot groupTimeSlot) {
        GroupCancelEvent event = groupCancelEventMapper.map(groupCancel, groupTimeSlot);
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);
            log.info("Group cancel event published: {}", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
