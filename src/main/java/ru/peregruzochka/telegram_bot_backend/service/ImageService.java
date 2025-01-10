package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Image;
import ru.peregruzochka.telegram_bot_backend.repository.ImageRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public Image getImageById(UUID imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(
                () -> new RuntimeException("Image not found")
        );
        log.info("Retrieved image with id {}", imageId);
        return image;
    }
}
