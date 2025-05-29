package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Child;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {

    @Query("""
            select r from Registration r
            where r.user = ?1
            and not exists (
                select 1 from Cancel c
                where c.registration = r
            )
            and r.timeslot.startTime > CURRENT_TIMESTAMP
            order by r.timeslot.startTime
            """)
    List<Registration> findAllActualByUser(User user);

    @Query("""
            select r from Registration r
            where not exists (
                select 1 from Cancel c
                where c.registration = r
            )
            and r.timeslot.startTime >= :from
            and r.timeslot.startTime <= :to
            order by r.timeslot.startTime
            """)
    List<Registration> findAllActualByDate(LocalDateTime from, LocalDateTime to);

    @Query("""
            select r from Registration r
            where not exists (
                select 1 from Cancel c
                where c.registration = r
            )
            and r.timeslot.teacher = :teacher
            and r.timeslot.startTime >= :from
            and r.timeslot.startTime <= :to
            order by r.timeslot.startTime
            """)
    List<Registration> findAllActualByTeacherByDate(Teacher teacher, LocalDateTime from, LocalDateTime to);

    @Query("""
            select r from Registration r
            where r.timeslot.startTime >= :start
            and r.timeslot.startTime <= :end
            order by r.timeslot.startTime
            """)
    List<Registration> findBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select r from Registration r
            where r.timeslot.startTime < :time
            and r.confirmStatus = "NOT_CONFIRMED"
            """)
    List<Registration> findNotConfirmedAfterTime(LocalDateTime time);

    @Query("""
            select r from Registration r
            where r.timeslot.startTime < :time
            and r.confirmStatus = "FIRST_QUESTION"
            """)
    List<Registration> findFirstQuestionAfterTime(LocalDateTime time);

    @Query("""
            select r from Registration r
            where r.timeslot.startTime < :time
            and r.confirmStatus = "SECOND_QUESTION"
            """)
    List<Registration> findSecondQuestionAfterTime(LocalDateTime time);

    @Query("""
            select r from Registration r
            where r.timeslot.startTime > :start
            and r.timeslot.startTime < :end
            and r.confirmStatus = "AUTO_CONFIRMED"
            """)
    List<Registration> findAutoConfirmedBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select r from Registration r
            where r.child = :child
            and (
                :from < r.timeslot.startTime and :to > r.timeslot.startTime and :from < r.timeslot.endTime and :to < r.timeslot.endTime
                or
                :from = r.timeslot.startTime and :to > r.timeslot.startTime and :from < r.timeslot.endTime and :to = r.timeslot.endTime
                or
                :from > r.timeslot.startTime and :to > r.timeslot.startTime and :from < r.timeslot.endTime and :to > r.timeslot.endTime
            )
            """)
    List<Registration> findOverlappingByChild(Child child, LocalDateTime from, LocalDateTime to);

    @Query("""
            select r from Registration r
            where r.child = :child
            and r.timeslot.teacher = :teacher
            and r.timeslot.startTime <= :from
            and r.timeslot.startTime <= :to
            """)
    List<Registration> findByChildAndTeacherBetweenTimes(Child child, Teacher teacher, LocalDateTime from, LocalDateTime to);
}
