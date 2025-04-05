package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupTimeSlotRepository extends JpaRepository<GroupTimeSlot, UUID> {

    @Query("""
        select t from GroupTimeSlot t
        where t.teacher = :teacher
        and (t.startTime <= :from and t.endTime > :from or t.startTime < :to and t.endTime >= :to)
        """)
    List<GroupTimeSlot> findOverlappingTimeSlots(Teacher teacher, LocalDateTime from, LocalDateTime to);

    List<GroupTimeSlot> getGroupTimeSlotByTeacherAndStartTimeBetween(Teacher teacher, LocalDateTime start, LocalDateTime end);
}
