package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.ChildDto;
import ru.peregruzochka.telegram_bot_backend.model.Child;

import java.util.List;

@Component
public class ChildMapper {

    public ChildDto toChildDto(Child child) {
        return ChildDto.builder()
                .id(child.getId())
                .name(child.getChildName())
                .birthday(child.getBirthday())
                .build();
    }

    public List<ChildDto> toChildDtoList(List<Child> childList) {
        return childList.stream()
                .map(this::toChildDto)
                .toList();
    }
}
