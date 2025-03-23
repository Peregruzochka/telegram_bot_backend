package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.peregruzochka.telegram_bot_backend.model.UserStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private Long telegramId;
    private String name;
    private String phone;
    private UserStatus status;
    private List<ChildDto> children;
}
