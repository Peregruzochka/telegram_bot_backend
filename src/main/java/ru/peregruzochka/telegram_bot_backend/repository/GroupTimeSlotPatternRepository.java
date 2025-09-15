package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupTimeSlotPatternRepository extends JpaRepository<GroupTimeSlotPattern, UUID> {

    @Query("""
            select t from GroupTimeSlotPattern t
            where t.dayOfWeek = :day
            and (
                     :from < t.startTime and :to > t.startTime and :from < t.endTime and :to < t.endTime
                     or
                     :from = t.startTime and :to > t.startTime and :from < t.endTime and :to = t.endTime
                     or
                     :from > t.startTime and :to > t.startTime and :from < t.endTime and :to > t.endTime
                 )
            """)
    List<GroupTimeSlotPattern> findOverLappingPatterns(DayOfWeek day, LocalTime from, LocalTime to);

    @Query("""
            select t from GroupTimeSlotPattern t
            where t.teacher = :teacher
            and t.dayOfWeek = :day
            order by t.startTime
            """)
    List<GroupTimeSlotPattern> findByTeacherAndDayOfWeek(Teacher teacher, DayOfWeek day);

    List<GroupTimeSlotPattern> findByDayOfWeek(DayOfWeek day);
}
