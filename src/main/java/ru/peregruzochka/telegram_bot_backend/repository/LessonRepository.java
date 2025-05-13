package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Lesson;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    @Query("""
            select l from Lesson l
            where :teacher member of l.teachers
            order by l.name asc
            """)
    List<Lesson> findAllByTeacher(Teacher teacher);
}
