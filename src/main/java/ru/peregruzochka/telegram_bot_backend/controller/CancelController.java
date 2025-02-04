package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.CancelDto;
import ru.peregruzochka.telegram_bot_backend.mapper.CancelMapper;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;
import ru.peregruzochka.telegram_bot_backend.service.CancelService;

@RestController
@RequestMapping("/cancellations")
@RequiredArgsConstructor
public class CancelController {
    private final CancelMapper cancelMapper;
    private final CancelService cancelService;

    @PostMapping
    CancelDto addCancel(@RequestBody CancelDto cancelDto) {
        Cancel cancel = cancelMapper.toCancelEntity(cancelDto);
        Cancel savedCancel = cancelService.addCancel(cancel);
        return cancelMapper.toCancelDto(savedCancel);
    }
}
