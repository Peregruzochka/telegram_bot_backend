package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.peregruzochka.telegram_bot_backend.dto.ImageDto;
import ru.peregruzochka.telegram_bot_backend.model.Image;

import java.io.IOException;

@Slf4j
@Component
public class ImageMapper {

    public Image toImageEntity(MultipartFile image) {
        try {
            return Image.builder()
                    .imageData(image.getBytes())
                    .imageName(image.getOriginalFilename())
                    .imageSize(image.getSize())
                    .build();

        } catch (IOException e) {
            log.error("Failed to convert image to ImageEntity", e);
            throw new RuntimeException();
        }
    }

    public ImageDto toImageDto(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .filename(image.getImageName())
                .image(image.getImageData())
                .build();
    }

}
