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
import ru.peregruzochka.telegram_bot_backend.redis.NewRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.repository.ChildRepository;
import ru.peregruzochka.telegram_bot_backend.repository.LessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.RegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto.RegistrationType.NEW_USER;
import static ru.peregruzochka.telegram_bot_backend.dto.RegistrationDto.RegistrationType.REGULAR_USER;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.FIRST_QUESTION;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.NOT_CONFIRMED;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final LessonRepository lessonRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ChildRepository childRepository;
    private final NewRegistrationEventPublisher newRegistrationEventPublisher;

    @Transactional
    public Registration addRegistration(Registration registration) {
        if (registration.getType().equals(NEW_USER)) {
            Child newChild = registration.getChild();
            User newUser = registration.getUser();
            newUser.setChildren(List.of(newChild));
            newChild.setParent(newUser);
            userRepository.save(newUser);

        } else if (registration.getType().equals(REGULAR_USER)) {
            Long telegramId = registration.getUser().getTelegramId();
            User regularUser = userRepository.findByTelegramId(telegramId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!regularUser.equals(registration.getUser())) {
                regularUser.setUserName(registration.getUser().getUserName());
                regularUser.setPhone(registration.getUser().getPhone());
            }


            Child registrationChild = registration.getChild();

            if (registrationChild.getId() == null) {
                registrationChild.setParent(regularUser);
                childRepository.save(registrationChild);
            } else {
                UUID childId = registrationChild.getId();
                Child existChild = childRepository.findById(childId)
                        .orElseThrow(() -> new IllegalArgumentException("Child not found"));
                if (!existChild.equals(registrationChild)) {
                    existChild.setChildName(registrationChild.getChildName());
                    existChild.setBirthday(registrationChild.getBirthday());
                    childRepository.save(existChild);
                }
            }

            userRepository.save(regularUser);
            registration.setUser(regularUser);
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

        registration.setConfirmStatus(NOT_CONFIRMED);
        Registration savedRegistration = registrationRepository.save(registration);

        log.info("Registration added: {}", savedRegistration);
        newRegistrationEventPublisher.publish(savedRegistration);
        return savedRegistration;
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllUserRegistration(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Registration> registrations = registrationRepository.findAllActiveByUser(user);
        log.info("Registrations found: {}", registrations.size());
        return registrations;
    }

    @Transactional
    public Registration updateRegistration(Registration registration) {
        UUID currentRegistrationId = registration.getId();
        Registration currentRegistration = registrationRepository.findById(currentRegistrationId).orElseThrow(
                () -> new IllegalArgumentException("Registration not found")
        );

        TimeSlot currentTimeSlot = currentRegistration.getTimeslot();
        currentTimeSlot.setIsAvailable(true);
        timeSlotRepository.save(currentTimeSlot);
        log.info("Time slot is available: {}", currentTimeSlot);

        UUID newTimeSlotId = registration.getTimeslot().getId();
        TimeSlot newTimeSlot = timeSlotRepository.findById(newTimeSlotId).orElseThrow(
                () -> new IllegalArgumentException("TimeSlot not found")
        );
        newTimeSlot.setIsAvailable(false);

        currentRegistration.setTimeslot(newTimeSlot);

        Registration updatedRegistration = registrationRepository.save(currentRegistration);
        log.info("Registration updated: {}", updatedRegistration);
        return updatedRegistration;
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrationByToday() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Registration> registrations = registrationRepository.findBetween(start, end);
        log.info("Today registrations found: {}", registrations.size());
        return registrations;
    }

    @Transactional
    public List<Registration> getNotConfirmed() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        List<Registration> registrations = registrationRepository.findNotConfirmedAfterTime(time);
        log.info("NOT_CONFIRMED registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(FIRST_QUESTION));
        registrationRepository.saveAll(registrations);
        return registrations;
    }
}
