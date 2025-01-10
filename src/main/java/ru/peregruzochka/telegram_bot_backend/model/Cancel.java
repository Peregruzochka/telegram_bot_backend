package ru.peregruzochka.telegram_bot_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cancel")
@Getter
@Setter
public class Cancel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @Column(name = "case_description", nullable = false, columnDefinition = "TEXT")
    private String caseDescription;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
