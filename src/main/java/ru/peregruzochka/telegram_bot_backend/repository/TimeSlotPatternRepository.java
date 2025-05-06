package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlotPattern;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSlotPatternRepository extends JpaRepository<TimeSlotPattern, UUID> {

    @Query("""
            select t from TimeSlotPattern t
            where t.teacher = :teacher
            and t.dayOfWeek = :day
            and (
                     :from < t.startTime and :to > t.startTime and :from < t.endTime and :to < t.endTime
                     or
                     :from = t.startTime and :to > t.startTime and :from < t.endTime and :to = t.endTime
                     or
                     :from > t.startTime and :to > t.startTime and :from < t.endTime and :to > t.endTime
                 )
            """)
    List<TimeSlotPattern> findOverLappingPatterns(Teacher teacher, DayOfWeek day, LocalTime from, LocalTime to);

    @Query("""
            select t from TimeSlotPattern t
            where t.teacher = :teacher
            and t.dayOfWeek = :day
            order by t.startTime
            """)
    List<TimeSlotPattern> findByTeacherAndDayOfWeek(Teacher teacher, DayOfWeek day);
}
