package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupCancel;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.redis.GroupCancelEventPublisher;
import ru.peregruzochka.telegram_bot_backend.repository.GroupCancelRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupRegistrationRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupCancelService {
    private final GroupCancelRepository groupCancelRepository;
    private final GroupRegistrationRepository groupRegistrationRepository;
    private final GroupTimeSlotRepository groupTimeSlotRepository;
    private final GroupCancelEventPublisher groupCancelEventPublisher;

    @Transactional
    public GroupCancel addGroupCancel(UUID registrationId, String caseDescription) {
        GroupRegistration registration = groupRegistrationRepository.findById(registrationId).orElseThrow(
                () -> new IllegalArgumentException("Registration not found")
        );

        GroupTimeSlot groupTimeSlot = registration.getGroupTimeslot();
        List<GroupRegistration> groupRegistrations = groupTimeSlot.getRegistrations();
        List<GroupRegistration> updatedGroupRegistrations = new ArrayList<>(groupRegistrations.stream()
                .dropWhile(groupRegistration -> groupRegistration.getId().equals(registrationId))
                .toList());
        groupTimeSlot.setRegistrations(updatedGroupRegistrations);
        groupTimeSlotRepository.save(groupTimeSlot);

        registration.setGroupTimeslot(null);
        groupRegistrationRepository.save(registration);

        GroupCancel cancel = GroupCancel.builder()
                .groupRegistration(registration)
                .startTime(groupTimeSlot.getStartTime())
                .caseDescription(caseDescription)
                .createdAt(LocalDateTime.now())
                .build();

        GroupCancel savedCancel = groupCancelRepository.save(cancel);
        log.info("Saved group cancel: {}", savedCancel);

        groupCancelEventPublisher.publish(savedCancel, groupTimeSlot);
        return savedCancel;
    }
}
