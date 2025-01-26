package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Child;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.repository.LessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.RegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

import java.util.List;

import static ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto.RegistrationType.NEW_USER;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final LessonRepository lessonRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public Registration addRegistration(Registration registration) {
        if (registration.getType().equals(NEW_USER)) {
            Child newChild = registration.getChild();
            User newUser = registration.getUser();
            newUser.setChildren(List.of(newChild));
            newChild.setParent(newUser);
            userRepository.save(newUser);
        }

        Lesson registrationLesson = registration.getLesson();
        Lesson savedLesson = lessonRepository.findById(registrationLesson.getId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson does not exist"));
        registration.setLesson(savedLesson);

        TimeSlot registrationTimeSlot = registration.getTimeslot();
        TimeSlot savedTimeSlot = timeSlotRepository.findById(registrationTimeSlot.getId())
                .orElseThrow(() -> new IllegalArgumentException("TimeSlot does not exist"));
        savedTimeSlot.setIsAvailable(false);
        registration.setTimeslot(savedTimeSlot);

        registration.setConfirmed(false);
        Registration savedRegistration = registrationRepository.save(registration);

        log.info("Registration added: {}", savedRegistration);
        return savedRegistration;
    }
}
