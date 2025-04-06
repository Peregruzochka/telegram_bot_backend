package ru.peregruzochka.telegram_bot_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupRegistrationEvent {
    private String username;
    private String userStatus;
    private String childName;
    private String childBirthday;
    private String teacherName;
    private String lessonName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int capacity;
    private int amount;
}
