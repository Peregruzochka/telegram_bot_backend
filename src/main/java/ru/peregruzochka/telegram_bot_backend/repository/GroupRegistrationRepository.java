package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Child;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRegistrationRepository extends JpaRepository<GroupRegistration, UUID> {

    @Query("""
            select r from GroupRegistration r
            where r.user = ?1
            and not exists (
                select 1 from GroupCancel c
                where c.groupRegistration = r
            )
            and r.groupTimeslot.startTime > CURRENT_TIMESTAMP
            order by r.groupTimeslot.startTime
            """)
    List<GroupRegistration> findAllActualByUser(User user);

    @Query("""
            select r from GroupRegistration r
            where not exists (
                select 1 from GroupCancel c
                where c.groupRegistration = r
            )
            and r.groupTimeslot.startTime >= :from
            and r.groupTimeslot.endTime <= :to
            order by r.groupTimeslot.startTime
            """)
    List<GroupRegistration> findAllActualByDate(LocalDateTime from, LocalDateTime to);

    @Query("""
            select r from GroupRegistration r
            where not exists (
                select 1 from GroupCancel c
                where c.groupRegistration = r
            )
            and r.groupTimeslot.teacher = :teacher
            and r.groupTimeslot.startTime >= :from
            and r.groupTimeslot.endTime <= :to
            order by r.groupTimeslot.startTime
            """)
    List<GroupRegistration> findAllActualByTeacherByDate(Teacher teacher, LocalDateTime from, LocalDateTime to);

    @Query("""
           select r from GroupRegistration r
           where r.groupTimeslot.startTime < :time
           and r.confirmStatus = "NOT_CONFIRMED"
           """)
    List<GroupRegistration> findNotConfirmedAfterTime(LocalDateTime time);

    @Query("""
            select r from GroupRegistration r
            where r.groupTimeslot.startTime < :time
            and r.confirmStatus = "FIRST_QUESTION"
            """)
    List<GroupRegistration> findFirstQuestionAfterTime(LocalDateTime time);

    @Query("""
            select r from GroupRegistration r
            where r.groupTimeslot.startTime < :time
            and r.confirmStatus = "SECOND_QUESTION"
            """)
    List<GroupRegistration> findSecondQuestionAfterTime(LocalDateTime time);
    @Query("""
            select r from GroupRegistration r
            where r.groupTimeslot.startTime > :start
            and r.groupTimeslot.startTime < :end
            and r.confirmStatus = "AUTO_CONFIRMED"
            """)
    List<GroupRegistration> findAutoConfirmedBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select r from GroupRegistration r
            where r.child = :child
            and (
                :from < r.groupTimeslot.startTime and :to > r.groupTimeslot.startTime and :from < r.groupTimeslot.endTime and :to < r.groupTimeslot.endTime
                or
                :from = r.groupTimeslot.startTime and :to > r.groupTimeslot.startTime and :from < r.groupTimeslot.endTime and :to = r.groupTimeslot.endTime
                or
                :from > r.groupTimeslot.startTime and :to > r.groupTimeslot.startTime and :from < r.groupTimeslot.endTime and :to > r.groupTimeslot.endTime
            )
            """)
    List<GroupRegistration> findOverlappingByChild(Child child, LocalDateTime from, LocalDateTime to);
}