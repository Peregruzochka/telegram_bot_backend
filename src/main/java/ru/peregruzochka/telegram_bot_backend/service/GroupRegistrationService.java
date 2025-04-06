package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.Child;
import ru.peregruzochka.telegram_bot_backend.model.ChildStatus;
import ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.User;
import ru.peregruzochka.telegram_bot_backend.model.UserStatus;
import ru.peregruzochka.telegram_bot_backend.redis.NewGroupRegistrationEventPublisher;
import ru.peregruzochka.telegram_bot_backend.repository.ChildRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupRegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.AUTO_CONFIRMED;
import static ru.peregruzochka.telegram_bot_backend.model.ConfirmStatus.NOT_CONFIRMED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupRegistrationService {

    private final GroupTimeSlotRepository groupTimeSlotRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final GroupRegistrationRepository groupRegistrationRepository;
    private final NewGroupRegistrationEventPublisher newGroupRegistrationEventPublisher;

    @Transactional
    public GroupRegistration addGroupRegistration(GroupRegistration groupRegistration) {
        UUID groupTimeslotId = groupRegistration.getGroupTimeslot().getId();
        GroupTimeSlot groupTimeSlot = getGroupTimeSlot(groupTimeslotId);

        checkLessonRegistrationAmount(groupTimeSlot);
        groupRegistration.setGroupTimeslot(groupTimeSlot);

        User user = groupRegistration.getUser();
        User dbUser = computeUser(user);

        Child child = groupRegistration.getChild();
        computeChild(child, dbUser);
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

    private void computeChild(Child child, User user) {
        switch (child.getStatus()) {
            case NEW -> {
                child.setParent(user);
                user.getChildren().add(child);
                childRepository.save(child);
            }
            case REGULAR -> checkChild(child);

            case EDITING -> {
                checkChild(child);
                Child editingChild = childRepository.findById(child.getId()).orElseThrow();
                editingChild.setChildName(child.getChildName());
                editingChild.setBirthday(child.getBirthday());
                editingChild.setStatus(ChildStatus.REGULAR);
                childRepository.save(editingChild);
            }
        }
    }

    private void checkChild(Child child) {
        childRepository.findById(child.getId()).orElseThrow(
                () -> new IllegalArgumentException("Child not found: " + child.getId())
        );


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
