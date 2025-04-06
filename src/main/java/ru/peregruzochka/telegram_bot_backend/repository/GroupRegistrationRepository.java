package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;
import ru.peregruzochka.telegram_bot_backend.model.User;

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
}
