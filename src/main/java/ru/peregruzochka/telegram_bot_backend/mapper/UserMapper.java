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
                .status(user.getStatus())
                .children(childMapper.toChildDtoList(user.getChildren()))
                .build();
    }

    public User toUserEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .telegramId(userDto.getTelegramId())
                .userName(userDto.getName())
                .status(userDto.getStatus())
                .phone(userDto.getPhone())
                .build();
    }
}
