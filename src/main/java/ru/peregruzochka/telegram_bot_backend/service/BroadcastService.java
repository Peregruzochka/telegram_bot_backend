package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Broadcast;
import ru.peregruzochka.telegram_bot_backend.model.BroadcastDelivery;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.repository.BroadcastDeliveryRepository;
import ru.peregruzochka.telegram_bot_backend.repository.BroadcastRepository;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BroadcastService {

    private final UserRepository userRepository;
    private final BroadcastRepository broadcastRepository;
    private final BroadcastDeliveryRepository broadcastDeliveryRepository;

    @Transactional
    public Broadcast createBroadcast(String text) {
        List<UUID> userIds = userRepository.findAllUsersId();
        long usersCount = userIds.size();

        Broadcast broadcast = Broadcast.builder()
                .text(text)
                .readCount(0L)
                .usersCount(usersCount)
                .build();

        Broadcast createdBroadcast = broadcastRepository.saveAndFlush(broadcast);

        List<BroadcastDelivery> deliveries = userIds.stream()
                .map(userId -> BroadcastDelivery.builder()
                        .broadcast(createdBroadcast)
                        .isRead(false)
                        .user(User.builder().id(userId).build())  // можно без загрузки full user
                        .build())
                .toList();

        broadcastDeliveryRepository.saveAll(deliveries);

        return createdBroadcast;
    }

    @Transactional(readOnly = true)
    public Broadcast getBroadcast(UUID broadcastId) {
        return broadcastRepository.findById(broadcastId)
                .orElseThrow(() -> new IllegalArgumentException("Broadcast not found"));
    }
}
