package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.GroupRegistration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTimeSlotDto {
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GroupLessonDto groupLesson;
    private TeacherDto teacher;
    private int registrationAmount;
}

