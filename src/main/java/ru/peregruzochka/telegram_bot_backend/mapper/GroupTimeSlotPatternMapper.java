package ru.peregruzochka.telegram_bot_backend.mapper;

import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupTimeSlotPatternDto;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;

import java.util.List;

@Component
public class GroupTimeSlotPatternMapper {

    public GroupTimeSlotPatternDto toTimeSlotPatternDto(GroupTimeSlotPattern timeSlotPattern) {
        return GroupTimeSlotPatternDto.builder()
                .id(timeSlotPattern.getId())
                .startTime(timeSlotPattern.getStartTime())
                .endTime(timeSlotPattern.getEndTime())
                .dayOfWeek(timeSlotPattern.getDayOfWeek())
                .teacherId(timeSlotPattern.getTeacher().getId())
                .lessonId(timeSlotPattern.getGroupLesson().getId())
                .build();
    }

    public List<GroupTimeSlotPatternDto> toTimeSlotPatternDtoList(List<GroupTimeSlotPattern> timeSlotPatterns) {
        return timeSlotPatterns.stream()
                .map(this::toTimeSlotPatternDto)
                .toList();
    }
}
