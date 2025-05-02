package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.TimeSlotPatternDto;
import ru.peregruzochka.telegram_bot_backend.mapper.TimeSlotPatternMapper;
import ru.peregruzochka.telegram_bot_backend.model.DayOfWeek;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.service.TimeSlotPatternService;

import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/timeslot_patterns")
@RequiredArgsConstructor
public class TimeSlotPattenController {
    private final TimeSlotPatternService timeSlotPatternService;
    private final TimeSlotPatternMapper timeSlotPatternMapper;

    @PostMapping
    TimeSlotPatternDto createTimeSlotPattern(@RequestParam("day") DayOfWeek day,
                                             @RequestParam("start-time") LocalTime start,
                                             @RequestParam("teacher-id") UUID teacherId) {
        TimeSlotPattern timeSlotPattern = timeSlotPatternService.createTimeSlotPattern(day, start, teacherId);
        return timeSlotPatternMapper.toTimeSlotPatternDto(timeSlotPattern);
    }
}
