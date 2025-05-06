package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.TimeSlotPatternDto;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlotPattern;

import java.util.List;

@Component
public class TimeSlotPatternMapper {

    public TimeSlotPatternDto toTimeSlotPatternDto(TimeSlotPattern timeSlotPattern) {
        return TimeSlotPatternDto.builder()
                .id(timeSlotPattern.getId())
                .startTime(timeSlotPattern.getStartTime())
                .endTime(timeSlotPattern.getEndTime())
                .dayOfWeek(timeSlotPattern.getDayOfWeek())
                .teacherId(timeSlotPattern.getTeacher().getId())
                .build();
    }

    public List<TimeSlotPatternDto> toTimeSlotPatternDtoList(List<TimeSlotPattern> timeSlotPatterns) {
        return timeSlotPatterns.stream()
                .map(this::toTimeSlotPatternDto)
                .toList();
    }
}
