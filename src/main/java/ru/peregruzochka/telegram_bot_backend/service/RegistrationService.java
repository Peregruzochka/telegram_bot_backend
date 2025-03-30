package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent;
import ru.peregruzochka.telegram_bot_backend.model.Child;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.model.UserStatus;
import ru.peregruzochka.telegram_bot_backend.redis.ConfirmRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.LocalCancelPublisher;
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
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CANCELLED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CONFIRMED_QR;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.FIRST_QUESTION;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.NOT_CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.SECOND_QUESTION;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.USER_CANCELLED;
import static ru.peregruzochka.telegram_bot_backend.model.UserStatus.NEW;


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
    private final ConfirmRegistrationEventPublisher confirmRegistrationEventPublisher;
    private final LocalCancelPublisher localCancelPublisher;

    @Value("${approve-delay.first-approve}")
    private int firstApproveDelay;

    @Value("${approve-delay.second-approve}")
    private int secondApproveDelay;

    @Value("${approve-delay.cancel-registration}")
    private int cancelRegistrationDelay;


    @Transactional
    public Registration addRegistration(Registration registration) {
        TimeSlot registrationTimeSlot = registration.getTimeslot();
        TimeSlot savedTimeSlot = timeSlotRepository.findById(registrationTimeSlot.getId())
                .orElseThrow(() -> new IllegalArgumentException("TimeSlot does not exist"));

        if (savedTimeSlot.getIsAvailable()) {
            savedTimeSlot.setIsAvailable(false);
            registration.setTimeslot(savedTimeSlot);
        } else {
            throw new IllegalArgumentException("TimeSlot is not available");
        }

        if (registration.getType().equals(NEW_USER)) {
            Child newChild = registration.getChild();
            User newUser = registration.getUser();
            newUser.setChildren(List.of(newChild));
            newChild.setParent(newUser);
            //потом убрать
            newUser.setStatus(NEW);
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

        registration.setConfirmStatus(NOT_CONFIRMED);

        if (LocalDateTime.now().plusDays(1).isAfter(registration.getTimeslot().getStartTime())) {
            registration.setConfirmStatus(AUTO_CONFIRMED);
        }

        registration.setCreatedAt(LocalDateTime.now());

        Registration savedRegistration = registrationRepository.save(registration);

        log.info("Registration added: {}", savedRegistration);
        newRegistrationEventPublisher.publish(savedRegistration);
        return savedRegistration;
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllActualRegistration(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Registration> registrations = registrationRepository.findAllActualByUser(user);
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

    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrationByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Registration> registrations = registrationRepository.findBetween(start, end);
        log.info("Registrations by {} found: {}", date, registrations.size());
        return registrations;
    }

    @Transactional
    public List<Registration> getNotConfirmed() {
        LocalDateTime time = LocalDateTime.now().plusHours(firstApproveDelay);

        List<Registration> registrations = registrationRepository.findNotConfirmedAfterTime(time);
        log.info("NOT_CONFIRMED registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(FIRST_QUESTION));
        registrationRepository.saveAll(registrations);
        return registrations;
    }

    @Transactional
    public List<Registration> getFirstQuestionRegistration() {
        LocalDateTime time = LocalDateTime.now()
                .plusHours(firstApproveDelay)
                .minusHours(secondApproveDelay);

        List<Registration> registrations = registrationRepository.findFirstQuestionAfterTime(time);
        log.info("FIRST_QUESTION registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(SECOND_QUESTION));
        registrationRepository.saveAll(registrations);
        return registrations;
    }

    @Transactional
    public List<Registration> getSecondQuestionRegistration() {
        LocalDateTime time = LocalDateTime.now()
                .plusHours(firstApproveDelay)
                .minusHours(secondApproveDelay)
                .minusHours(cancelRegistrationDelay);

        List<Registration> registrations = registrationRepository.findSecondQuestionAfterTime(time);
        log.info("SECOND_QUESTION registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(AUTO_CANCELLED));
        registrationRepository.saveAll(registrations);

        registrations.stream()
                .map(Registration::getId)
                .map(id -> LocalCancelEvent.builder()
                        .registrationId(id)
                        .caseDescription("Автоматическая отмена занятия")
                        .build()
                )
                .forEach(localCancelPublisher::publish);

        return registrations;
    }

    @Transactional
    public List<Registration> getAutoConfirmedRegistration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        List<Registration> registrations = registrationRepository.findAutoConfirmedBetween(start, end);
        log.info("AUTO_CONFIRMED registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(AUTO_CONFIRMED_QR));
        registrationRepository.saveAll(registrations);
        return registrations;
    }

    @Transactional
    public Registration confirm(UUID registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        if (registration.getConfirmStatus() == FIRST_QUESTION || registration.getConfirmStatus() == SECOND_QUESTION) {
            registration.setConfirmStatus(CONFIRMED);
            log.info("Confirm registration: {}", registration);
            confirmRegistrationEventPublisher.publish(registration);
            return registrationRepository.save(registration);
        } else {
            return registration;
        }
    }

    @Transactional
    public Registration decline(UUID registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        if (registration.getConfirmStatus() == FIRST_QUESTION || registration.getConfirmStatus() == SECOND_QUESTION) {
            registration.setConfirmStatus(USER_CANCELLED);
            log.info("Decline registration: {}", registration);

            LocalCancelEvent localCancelEvent = LocalCancelEvent.builder()
                    .registrationId(registrationId)
                    .caseDescription("Клиент отказался от занятия после вопроса о подтверждении")
                    .build();

            localCancelPublisher.publish(localCancelEvent);
            return registrationRepository.save(registration);
        } else {
            return registration;
        }
    }
}
