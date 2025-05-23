package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRegistrationDto {
    private UUID id;
    private ChildDto child;
    private UserDto user;
    private GroupTimeSlotDto timeSlot;
    private LocalDateTime createdAt;
}
