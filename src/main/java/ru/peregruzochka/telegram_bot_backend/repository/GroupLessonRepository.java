package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;

import java.util.UUID;

@Repository
public interface GroupLessonRepository extends JpaRepository<GroupLesson, UUID> {
}
