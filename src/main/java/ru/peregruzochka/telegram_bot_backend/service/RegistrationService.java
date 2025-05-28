package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent;
import ru.peregruzochka.telegram_bot_backend.model.Child;
import ru.peregruzochka.telegram_bot_backend.model.ChildStatus;
import ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.model.UserStatus;
import ru.peregruzochka.telegram_bot_backend.redis.ConfirmRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.LocalCancelPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.NewRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.repository.ChildRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupRegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.LessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.RegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent.CancelType.INDIVIDUAL;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CANCELLED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CONFIRMED_QR;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.FIRST_QUESTION;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.NOT_CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.SECOND_QUESTION;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.USER_CANCELLED;


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
    private final TeacherRepository teacherRepository;
    private final GroupRegistrationRepository groupRegistrationRepository;

    @Value("${approve-delay.first-approve}")
    private int firstApproveDelay;

    @Value("${approve-delay.second-approve}")
    private int secondApproveDelay;

    @Value("${approve-delay.cancel-registration}")
    private int cancelRegistrationDelay;

    @Transactional
    public Registration addRegistration(Registration registration) {
        checkLesson(registration.getLesson());
        checkTimeSlot(registration.getTimeslot());

        UUID teacherId = registration.getTimeslot().getTeacher().getId();
        Teacher dbTeacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found: " + teacherId)
        );

        registration.getTimeslot().setIsAvailable(false);
        registration.getTimeslot().setTeacher(dbTeacher);
        timeSlotRepository.save(registration.getTimeslot());

        User user = registration.getUser();
        User dbUser = computeUser(user);

        Child child = registration.getChild();
        computeChild(child, dbUser, registration);

        registration.setUser(dbUser);
        ConfirmStatus status = chooseConfirmStatus(registration);
        registration.setConfirmStatus(status);
        registration.setCreatedAt(LocalDateTime.now());
        Registration newRegistration = registrationRepository.save(registration);
        newRegistrationEventPublisher.publish(newRegistration);
        log.info("New registration: {}", newRegistration);
        return newRegistration;
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
                        .type(INDIVIDUAL)
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

    @Transactional(readOnly = true)
    public Registration getRegistrationById(UUID id) {
        Registration registration = registrationRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Registration not found")
        );
        log.info("Registration found: {}", registration);
        return registration;
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
                    .type(INDIVIDUAL)
                    .build();

            localCancelPublisher.publish(localCancelEvent);
            return registrationRepository.save(registration);
        } else {
            return registration;
        }
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllActualRegistrationByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Registration> registrations = registrationRepository.findAllActualByDate(start, end);
        log.info("All registrations by date [{}] found: {}", date, registrations.size());
        return registrations;
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllActualRegistrationByTeacherByDate(UUID teacherId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<Registration> registrations = registrationRepository.findAllActualByTeacherByDate(teacher, start, end);
        log.info("All registrations by teacher[{}] by date [{}] found: {}", teacher, date, registrations.size());
        return registrations;
    }


    private void computeChild(Child child, User user, Registration registration) {
        switch (child.getStatus()) {
            case NEW -> {
                child.setParent(user);
                user.getChildren().add(child);
                childRepository.save(child);
            }
            case REGULAR -> checkChild(child, registration);

            case EDITING -> {
                checkChild(child, registration);
                Child editingChild = childRepository.findById(child.getId()).orElseThrow();
                editingChild.setChildName(child.getChildName());
                editingChild.setBirthday(child.getBirthday());
                editingChild.setStatus(ChildStatus.REGULAR);
                childRepository.save(editingChild);
            }
        }
    }

    private User computeUser(User user) {
        return switch (user.getStatus()) {
            case NEW-> {
                user.setChildren(new ArrayList<>());
                yield userRepository.save(user);
            }

            case REGULAR -> {
                User regUser = checkUser(user);
                regUser.setStatus(UserStatus.REGULAR);
                yield regUser;
            }

            case EDITING -> {
                User dbUser = checkUser(user);
                dbUser.setUserName(user.getUserName());
                dbUser.setStatus(UserStatus.REGULAR);
                yield userRepository.save(dbUser);
            }
        };
    }

    private ConfirmStatus chooseConfirmStatus(Registration registration) {
        LocalDateTime startTime = registration.getTimeslot().getStartTime();
        if (LocalDateTime.now().plusDays(1).isAfter(startTime)) {
            return AUTO_CONFIRMED;
        } else  {
            return NOT_CONFIRMED;
        }
    }

    private void checkChild(Child child, Registration registration) {
        childRepository.findById(child.getId()).orElseThrow(
                () -> new IllegalArgumentException("Child not found: " + child.getId())
        );

        LocalDateTime startTime = registration.getTimeslot().getStartTime();
        if (registrationRepository.existsByChildAndTimeslot_StartTime(child, startTime) ||
                groupRegistrationRepository.existsGroupRegistrationByChildAndGroupTimeslot_StartTime(child, startTime)
        ) {
            throw new IllegalArgumentException("Registration already exists for the child: " + child);
        }
    }

    private User checkUser(User user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("User with id " + user.getId() + " not found")
        );
    }

    private void checkLesson(Lesson lesson) {
        lessonRepository.findById(lesson.getId()).orElseThrow(
                () -> new IllegalArgumentException("Lesson not found: " + lesson.getId())
        );
    }

    private void checkTimeSlot(TimeSlot timeSlot) {
        TimeSlot dbTimeSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow(
                () -> new IllegalArgumentException("TimeSlot not found: " + timeSlot.getId())
        );
        if (!dbTimeSlot.getIsAvailable()) {
            throw new IllegalArgumentException("TimeSlot is not available");
        }
    }


}
