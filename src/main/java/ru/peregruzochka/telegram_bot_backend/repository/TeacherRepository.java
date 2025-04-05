package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    @Query("""
            select distinct t from Teacher t
            join t.groupLessons gl
            """)
    List<Teacher> findTeachersByAllGroupLessons();
}
