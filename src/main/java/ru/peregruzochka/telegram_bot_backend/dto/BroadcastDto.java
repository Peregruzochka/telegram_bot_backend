package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BroadcastDto {
    private UUID id;
    private String text;
    private Long userCount;
    private Long readCount;
    private LocalDateTime createdAt;
}
