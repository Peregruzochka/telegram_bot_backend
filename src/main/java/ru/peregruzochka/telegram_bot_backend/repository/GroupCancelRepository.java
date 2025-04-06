package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.peregruzochka.telegram_bot_backend.model.GroupCancel;

import java.util.UUID;

public interface GroupCancelRepository extends JpaRepository<GroupCancel, UUID> {
}
