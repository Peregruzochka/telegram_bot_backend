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
public class RegistrationEvent {
    private String userName;
    private String childName;
    private String childrenBirthday;
    private String teacherName;
    private String registrationType;
    private String lessonName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
