package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.BroadcastCreateDto;
import ru.peregruzochka.telegram_bot_backend.dto.BroadcastDto;
import ru.peregruzochka.telegram_bot_backend.mapper.BroadcastMapper;
import ru.peregruzochka.telegram_bot_backend.model.Broadcast;
import ru.peregruzochka.telegram_bot_backend.service.BroadcastService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/broadcasts")
public class BroadcastController {

    private final BroadcastService broadcastService;
    private final BroadcastMapper broadcastMapper;

    @PostMapping
    public BroadcastDto createBroadcast(@RequestBody BroadcastCreateDto broadcastCreateDto) {
        Broadcast newBroadcast  = broadcastService.createBroadcast(broadcastCreateDto.getText());
        return broadcastMapper.toDto(newBroadcast);
    }

    @GetMapping("/{broadcastId}")
    public BroadcastDto getBroadcast(@PathVariable UUID broadcastId) {
        Broadcast broadcast = broadcastService.getBroadcast(broadcastId);
        return broadcastMapper.toDto(broadcast);
    }

    @GetMapping("/history")
    public List<Broadcast> getBroadcastHistory(@RequestParam(value = "telegram_id", required = false) String telegramId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size) {
        return null;
    }
}
