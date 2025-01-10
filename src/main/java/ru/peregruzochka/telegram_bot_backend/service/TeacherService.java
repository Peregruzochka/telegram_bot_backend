package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Image;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.repository.ImageRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {
    private final ImageRepository imageRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public Teacher addTeacher(String name, Image image) {
        Image savedImage = imageRepository.save(image);
        Teacher teacher = Teacher.builder()
                .name(name)
                .image(savedImage)
                .build();
        Teacher savedTeacher = teacherRepository.save(teacher);
        log.info("Teacher added: {}", savedTeacher);
        return savedTeacher;
    }
}
