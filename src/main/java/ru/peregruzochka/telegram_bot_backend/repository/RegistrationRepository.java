package ru.peregruzochka.telegram_bot_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.telegram_bot_backend.model.Registration;
import ru.peregruzochka.telegram_bot_backend.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    @Query("""
            select r from Registration r
            where r.user = ?1
            and r.type != "CANCEL"
            order by r.timeslot.startTime
            """)
    List<Registration> findAllActiveByUser(User user);

    @Query("""
           select r from Registration r
           where r.timeslot.startTime >= :start
           and r.timeslot.startTime <= :end
           order by r.timeslot.startTime
           """)
    List<Registration> findBetween(LocalDateTime start, LocalDateTime end);
}
