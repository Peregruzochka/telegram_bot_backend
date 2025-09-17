package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupTimeSlotRepository extends JpaRepository<GroupTimeSlot, UUID> {

    @Query("""
            select t from GroupTimeSlot t
            where t.teacher = :teacher
            and (
                :from < t.startTime and :to > t.startTime and :from < t.endTime and :to < t.endTime
                or
                :from = t.startTime and :to > t.startTime and :from < t.endTime and :to = t.endTime
                or
                :from > t.startTime and :to > t.startTime and :from < t.endTime and :to > t.endTime
            )
            """)
    List<GroupTimeSlot> findOverlappingTimeSlots(Teacher teacher, LocalDateTime from, LocalDateTime to);

    List<GroupTimeSlot> getGroupTimeSlotByTeacherAndStartTimeBetween(Teacher teacher, LocalDateTime start, LocalDateTime end);

    @Query("""
            select t from GroupTimeSlot t
            where t.teacher = :teacher
            and t.startTime >= :from and t.startTime <= :to
            and (t.groupLesson.groupSize > SIZE(t.registrations))
            order by t.startTime
            """)
    List<GroupTimeSlot> getAvailableByTeacher(Teacher teacher, LocalDateTime from, LocalDateTime to);


    @Query("""
            select distinct t from GroupTimeSlot t
            join t.registrations r
            where t.startTime >= :from and t.startTime <= :to
            and r.user = :user
            """)
    List<GroupTimeSlot> findByUserByDate(User user, LocalDateTime from, LocalDateTime to);

    @Query("""
            select t from GroupTimeSlot t
            where t.teacher = :teacher
            and t.groupLesson = :lesson
            and t.startTime >= :from and t.startTime <= :to
            and (t.groupLesson.groupSize > SIZE(t.registrations))
            order by t.startTime
            """)
    List<GroupTimeSlot> getAvailableByTeacherByLesson(Teacher teacher, GroupLesson lesson, LocalDateTime from, LocalDateTime to);

    List<GroupTimeSlot> findByStartTimeBetween(LocalDateTime fromTime, LocalDateTime toTime);

    List<GroupTimeSlot> findByTeacherAndStartTimeBetween(Teacher teacher, LocalDateTime fromTime, LocalDateTime toTime);
}
