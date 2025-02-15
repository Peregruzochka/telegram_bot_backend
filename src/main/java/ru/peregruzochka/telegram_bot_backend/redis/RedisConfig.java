package ru.peregruzochka.telegram_bot_backend.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final LocalCancelRegistrationListener localCancelRegistrationListener;

    @Value("${spring.data.redis-channel.local-cancel}")
    private String localCancelChannel;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(
                new MessageListenerAdapter(localCancelRegistrationListener),
                new ChannelTopic(localCancelChannel)
        );

        return container;
    }
}
