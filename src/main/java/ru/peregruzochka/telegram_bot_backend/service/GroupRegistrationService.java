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
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.model.UserStatus;
import ru.peregruzochka.telegram_bot_backend.redis.ConfirmGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.LocalCancelPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.NewGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.redis.NotConfirmedGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.repository.ChildRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupRegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.RegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.dto.LocalCancelEvent.CancelType.GROUP;
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
public class GroupRegistrationService {

    private final GroupTimeSlotRepository groupTimeSlotRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final GroupRegistrationRepository groupRegistrationRepository;
    private final NewGroupRegistrationEventPublisher newGroupRegistrationEventPublisher;
    private final TeacherRepository teacherRepository;
    private final ConfirmGroupRegistrationEventPublisher confirmGroupRegistrationEventPublisher;
    private final LocalCancelPublisher localCancelPublisher;
    private final RegistrationRepository registrationRepository;

    @Value("${approve-delay.first-approve}")
    private int firstApproveDelay;

    @Value("${approve-delay.second-approve}")
    private int secondApproveDelay;

    @Value("${approve-delay.cancel-registration}")
    private int cancelRegistrationDelay;

    @Transactional
    public GroupRegistration addGroupRegistration(GroupRegistration groupRegistration) {
        UUID groupTimeslotId = groupRegistration.getGroupTimeslot().getId();
        GroupTimeSlot groupTimeSlot = getGroupTimeSlot(groupTimeslotId);

        checkLessonRegistrationAmount(groupTimeSlot);
        groupRegistration.setGroupTimeslot(groupTimeSlot);

        User user = groupRegistration.getUser();
        User dbUser = computeUser(user);

        Child child = groupRegistration.getChild();
        computeChild(child, dbUser, groupRegistration);
        checkChildUniqInTimeSlot(groupTimeSlot, child);

        groupRegistration.setUser(dbUser);
        ConfirmStatus status = chooseConfirmStatus(groupRegistration);
        groupRegistration.setConfirmStatus(status);
        groupRegistration.setCreatedAt(LocalDateTime.now());

        GroupRegistration newGroupRegistration = groupRegistrationRepository.save(groupRegistration);
        log.info("New group registration: {}", newGroupRegistration);

        if (groupTimeSlot.getRegistrations() == null) {
            groupTimeSlot.setRegistrations(new ArrayList<>());
        }

        groupTimeSlot.getRegistrations().add(newGroupRegistration);
        groupTimeSlotRepository.save(groupTimeSlot);

        newGroupRegistrationEventPublisher.publish(newGroupRegistration);
        return newGroupRegistration;
    }

    @Transactional(readOnly = true)
    public List<GroupRegistration> getAllActualRegistration(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

        List<GroupRegistration> registrations = groupRegistrationRepository.findAllActualByUser(user);
        log.info("All actual user {} registrations: {}", userId, registrations);
        return registrations;
    }

    @Transactional(readOnly = true)
    public GroupRegistration getRegistrationById(UUID groupRegistrationId) {
        GroupRegistration groupRegistration = groupRegistrationRepository.findById(groupRegistrationId).orElseThrow(
                () -> new IllegalArgumentException("GroupRegistration not found")
        );
        log.info("Get group registration: {}", groupRegistration);
        return groupRegistration;
    }

    @Transactional(readOnly = true)
    public List<GroupRegistration> getAllActualRegistrationByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<GroupRegistration> registrations = groupRegistrationRepository.findAllActualByDate(start, end);
        log.info("All actual group registration by date [{}] {}", date, registrations);
        return registrations;
    }

    @Transactional(readOnly = true)
    public List<GroupRegistration> getAllActualRegistrationByTeacherByDate(UUID teacherId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );

        List<GroupRegistration> registrations = groupRegistrationRepository.findAllActualByTeacherByDate(teacher, start, end);
        log.info("All actual group registration by teacher {} by date [{}] {}", teacher, date, registrations);
        return registrations;
    }

    @Transactional
    public List<GroupRegistration> getNotConfirmed() {
        LocalDateTime time = LocalDateTime.now().plusHours(firstApproveDelay);

        List<GroupRegistration> registrations = groupRegistrationRepository.findNotConfirmedAfterTime(time);
        log.info("NOT_CONFIRMED group registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(FIRST_QUESTION));
        groupRegistrationRepository.saveAll(registrations);
        return registrations;
    }

    @Transactional
    public List<GroupRegistration> getFirstQuestionRegistration() {
        LocalDateTime time = LocalDateTime.now()
                .plusHours(firstApproveDelay)
                .minusHours(secondApproveDelay);

        List<GroupRegistration> registrations = groupRegistrationRepository.findFirstQuestionAfterTime(time);
        log.info("FIRST_QUESTION group registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(SECOND_QUESTION));
        groupRegistrationRepository.saveAll(registrations);
        return registrations;
    }

    @Transactional
    public List<GroupRegistration> getSecondQuestionRegistration() {
        LocalDateTime time = LocalDateTime.now()
                .plusHours(firstApproveDelay)
                .minusHours(secondApproveDelay)
                .minusHours(cancelRegistrationDelay);

        List<GroupRegistration> registrations = groupRegistrationRepository.findSecondQuestionAfterTime(time);
        log.info("SECOND_QUESTION group registrations found: {}", registrations.size());
        registrations.forEach(registration -> registration.setConfirmStatus(AUTO_CANCELLED));
        groupRegistrationRepository.saveAll(registrations);

        registrations.stream()
                .map(GroupRegistration::getId)
                .map(id -> LocalCancelEvent.builder()
                        .registrationId(id)
                        .caseDescription("Автоматическая отмена занятия")
                        .type(GROUP)
                        .build()
                )
                .forEach(localCancelPublisher::publish);

        return registrations;
    }

    @Transactional
    public List<GroupRegistration> getAutoConfirmedRegistration() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        List<GroupRegistration> groupRegistrations = groupRegistrationRepository.findAutoConfirmedBetween(start, end);
        log.info("AUTO_CONFIRMED group registrations found: {}", groupRegistrations.size());
        groupRegistrations.forEach(registration -> registration.setConfirmStatus(AUTO_CONFIRMED_QR));
        groupRegistrationRepository.saveAll(groupRegistrations);
        return groupRegistrations;
    }

    @Transactional
    public GroupRegistration confirm(UUID registrationId) {
        GroupRegistration groupRegistration = groupRegistrationRepository.findById(registrationId).orElseThrow(
                () -> new IllegalArgumentException("Registration not found")
        );
        if (groupRegistration.getConfirmStatus() == FIRST_QUESTION
                || groupRegistration.getConfirmStatus() == SECOND_QUESTION) {
            groupRegistration.setConfirmStatus(CONFIRMED);
            log.info("Confirm group registration: {}", groupRegistration);
            confirmGroupRegistrationEventPublisher.publish(groupRegistration);
            return groupRegistrationRepository.save(groupRegistration);
        } else {
            return groupRegistration;
        }
    }

    @Transactional
    public GroupRegistration decline(UUID registrationId) {
        GroupRegistration groupRegistration = groupRegistrationRepository.findById(registrationId).orElseThrow(
                () -> new IllegalArgumentException("Registration not found")
        );
        if (groupRegistration.getConfirmStatus() == FIRST_QUESTION || groupRegistration.getConfirmStatus() == SECOND_QUESTION) {
            groupRegistration.setConfirmStatus(USER_CANCELLED);
            log.info("Decline group registration: {}", groupRegistration);

            LocalCancelEvent cancelEvent = LocalCancelEvent.builder()
                    .registrationId(groupRegistration.getId())
                    .caseDescription("Клиент отказался от занятия после вопроса о подтверждении")
                    .type(GROUP)
                    .build();

            localCancelPublisher.publish(cancelEvent);
            return groupRegistrationRepository.save(groupRegistration);
        } else {
            return groupRegistration;
        }
    }

    private void checkChildUniqInTimeSlot(GroupTimeSlot groupTimeSlot, Child child) {
        List<UUID> childIds = groupTimeSlot.getRegistrations().stream()
                .map(GroupRegistration::getChild)
                .map(Child::getId)
                .toList();
        if (childIds.contains(child.getId())) {
            throw new RuntimeException("Duplicate child registration found");
        }
    }

    private void checkLessonRegistrationAmount(GroupTimeSlot groupTimeSlot) {
        int lessonCapacity = groupTimeSlot.getGroupLesson().getGroupSize();
        int registrationAmount = groupTimeSlot.getRegistrations() == null ? 0 : groupTimeSlot.getRegistrations().size();
        if (registrationAmount >= lessonCapacity) {
            throw new IllegalArgumentException("Lesson capacity exceeded");
        }
    }

    private GroupTimeSlot getGroupTimeSlot(UUID groupTimeslotId) {
        return groupTimeSlotRepository.findById(groupTimeslotId).orElseThrow(
                () -> new IllegalArgumentException("GroupTimeSlot not found")
        );
    }

    private User computeUser(User user) {
        return switch (user.getStatus()) {
            case NEW -> {
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

    private User checkUser(User user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("User with id " + user.getId() + " not found")
        );
    }

    private void computeChild(Child child, User user, GroupRegistration registration) {
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

    private void checkChild(Child child, GroupRegistration groupRegistration) {
        childRepository.findById(child.getId()).orElseThrow(
                () -> new IllegalArgumentException("Child not found: " + child.getId())
        );

        LocalDateTime startTime = groupRegistration.getGroupTimeslot().getStartTime();
        if (registrationRepository.existsByChildAndTimeslot_StartTime(child, startTime) ||
                groupRegistrationRepository.existsGroupRegistrationByChildAndGroupTimeslot_StartTime(child, startTime)
        ) {
            throw new IllegalArgumentException("Registration already exists for the child: " + child);
        }
    }

    private ConfirmStatus chooseConfirmStatus(GroupRegistration registration) {
        LocalDateTime startTime = registration.getGroupTimeslot().getStartTime();
        if (LocalDateTime.now().plusDays(1).isAfter(startTime)) {
            return AUTO_CONFIRMED;
        } else {
            return NOT_CONFIRMED;
        }
    }


}
