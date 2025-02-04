package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {

    @Query("""
        select t from TimeSlot t
        where t.teacher = :teacher
        and t.isAvailable = true
        and t.startTime >= :from
        and t.endTime <= :to
        """)
    List<TimeSlot> getTeacherAvailableTimeSlots(Teacher teacher, LocalDateTime from, LocalDateTime to);

    @Query("""
        select t from TimeSlot t
        where t.teacher = :teacher
        and (t.startTime <= :from and t.endTime > :from or t.startTime < :to and t.endTime >= :to)
        """)
    List<TimeSlot> findOverlappingTimeSlots(Teacher teacher, LocalDateTime from, LocalDateTime to);

    @Query("""
        select t from TimeSlot t
        where t.teacher = :teacher
        and t.startTime >= :from
        and t.endTime <= :to
        """)
    List<TimeSlot> getTeacherAllTimeSlots(Teacher teacher, LocalDateTime from, LocalDateTime to);
}
