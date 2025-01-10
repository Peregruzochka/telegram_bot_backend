package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.UserDto;
import ru.peregruzochka.telegram_bot_backend.model.User;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ChildMapper childMapper;

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .telegramId(user.getTelegramId())
                .name(user.getUserName())
                .phone(user.getPhone())
                .children(childMapper.toChildDtoList(user.getChildren()))
                .build();
    }


}
