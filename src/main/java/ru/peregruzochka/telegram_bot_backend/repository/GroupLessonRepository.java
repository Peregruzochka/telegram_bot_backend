package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupLessonRepository extends JpaRepository<GroupLesson, UUID> {

    @Query("""
            select g from GroupLesson g
            join g.teachers t
            where t = :teacher
            """)
    List<GroupLesson> findAllByTeacher(Teacher teacher);
}
