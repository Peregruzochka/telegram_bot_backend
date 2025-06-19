package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.Data;

@Data
public class BroadcastStatisticsDto {
    private Long usersCount;
    private Long readCount;
}
