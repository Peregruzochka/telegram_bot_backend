package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Cancel;

import java.util.UUID;

@Repository
public interface CancelRepository extends JpaRepository<Cancel, UUID> {
}
