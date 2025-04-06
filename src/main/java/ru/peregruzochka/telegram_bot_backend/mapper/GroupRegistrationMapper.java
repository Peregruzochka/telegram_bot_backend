package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupRegistrationDto;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupRegistrationMapper {

    private final ChildMapper childMapper;
    private final UserMapper userMapper;
    private final GroupTimeSlotMapper groupTimeSlotMapper;

    public GroupRegistration toGroupRegistrationEntity(GroupRegistrationDto groupRegistrationDto) {
        return GroupRegistration.builder()
                .id(groupRegistrationDto.getId())
                .child(childMapper.toChildEntity(groupRegistrationDto.getChild()))
                .user(userMapper.toUserEntity(groupRegistrationDto.getUser()))
                .groupTimeslot(groupTimeSlotMapper.toGroupTimeSlotEntity(groupRegistrationDto.getTimeSlot()))
                .build();
    }

    public GroupRegistrationDto toGroupRegistrationDto(GroupRegistration groupRegistration) {
        return GroupRegistrationDto.builder()
                .id(groupRegistration.getId())
                .child(childMapper.toChildDto(groupRegistration.getChild()))
                .user(userMapper.toUserDto(groupRegistration.getUser()))
                .timeSlot(groupTimeSlotMapper.toGroupTimeSlotDto(groupRegistration.getGroupTimeslot()))
                .createdAt(groupRegistration.getCreatedAt())
                .build();
    }

    public List<GroupRegistrationDto> toGroupRegistrationDtoList(List<GroupRegistration> groupRegistrations) {
        return groupRegistrations.stream()
                .map(this::toGroupRegistrationDto)
                .toList();
    }
}
