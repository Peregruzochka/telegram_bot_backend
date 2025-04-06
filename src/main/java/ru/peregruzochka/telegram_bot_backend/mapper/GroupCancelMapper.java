package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupCancelDto;
import ru.peregruzochka.telegram_bot_backend.model.GroupCancel;

@Component
@RequiredArgsConstructor
public class GroupCancelMapper {
    public GroupCancelDto mapToGroupCancelDto(GroupCancel groupCancel) {
        return GroupCancelDto.builder()
                .id(groupCancel.getId())
                .registrationId(groupCancel.getGroupRegistration().getId())
                .caseDescription(groupCancel.getCaseDescription())
                .build();
    }
}
