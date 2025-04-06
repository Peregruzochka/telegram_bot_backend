package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.GroupCancelDto;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupCancelMapper;
import ru.peregruzochka.telegram_bot_backend.model.GroupCancel;
import ru.peregruzochka.telegram_bot_backend.service.GroupCancelService;

import java.util.UUID;

@RestController
@RequestMapping("/group-cancellations")
@RequiredArgsConstructor
public class GroupCancelController {

    private final GroupCancelService groupCancelService;
    private final GroupCancelMapper groupCancelMapper;

    @PostMapping
    public GroupCancelDto addGroupCancel(@RequestParam("group-registration-id") UUID registrationId,
                                         @RequestParam("case") String caseDescription) {
        GroupCancel cancel = groupCancelService.addGroupCancel(registrationId, caseDescription);
        return groupCancelMapper.mapToGroupCancelDto(cancel);
    }
}
