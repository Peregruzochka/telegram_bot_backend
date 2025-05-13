package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    @Query("""
            select distinct t from Teacher t
            join t.groupLessons gl
            """)
    List<Teacher> findTeachersByAllGroupLessons();

    @Query("""
            select t from Teacher t
            where :lesson member of t.lessons
            order by t.name
            """)
    List<Teacher> findTeachersByLesson(Lesson lesson);

    @Query("""
            select t from Teacher t
            where :lesson member of t.groupLessons
            order by t.name
            """)
    List<Teacher> findTeachersByGroupLesson(GroupLesson lesson);

    @Query("""
            select distinct ts.teacher from TimeSlot ts
            where ts.isAvailable
            and ts.startTime >= :from and ts.startTime <= :to
            """)
    Set<Teacher> findByTimeSlotDate(LocalDateTime from, LocalDateTime to);

    @Query("""
            select distinct gts.teacher from GroupTimeSlot gts
            where size(gts.registrations) < gts.groupLesson.groupSize
            and gts.startTime >= :from and gts.startTime <= :to
            """)
    Set<Teacher> findByGroupTimeSlotDate(LocalDateTime from, LocalDateTime to);
}
