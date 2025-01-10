package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.ImageDto;
import ru.peregruzochka.telegram_bot_backend.mapper.ImageMapper;
import ru.peregruzochka.telegram_bot_backend.model.Image;
import ru.peregruzochka.telegram_bot_backend.service.ImageService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;
    private final ImageMapper imageMapper;

    @GetMapping("/{image-id}")
    public ImageDto getImageById(@PathVariable(name = "image-id") UUID imageId) {
        Image image = imageService.getImageById(imageId);
        return imageMapper.toImageDto(image);
    }
}
