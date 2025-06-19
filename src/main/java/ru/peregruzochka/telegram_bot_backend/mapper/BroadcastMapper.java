package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.BroadcastDto;
import ru.peregruzochka.telegram_bot_backend.model.Broadcast;

@Component
public class BroadcastMapper {

    public BroadcastDto toDto(Broadcast broadcast) {
        if (broadcast == null) {
            return null;
        }
        return BroadcastDto.builder()
                .id(broadcast.getId())
                .text(broadcast.getText())
                .userCount(broadcast.getUsersCount())
                .readCount(broadcast.getReadCount())
                .createdAt(broadcast.getCreatedAt())
                .build();
    }
}
