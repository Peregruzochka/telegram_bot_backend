package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.peregruzochka.telegram_bot_backend.model.ChildStatus;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChildDto {
    private UUID id;
    private String name;
    private String birthday;
    private ChildStatus status;
}
