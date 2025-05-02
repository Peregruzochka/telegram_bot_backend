package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.GroupTimeSlotPatternDto;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupTimeSlotPatternMapper;
import ru.peregruzochka.telegram_bot_backend.model.DayOfWeek;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlotPattern;
import ru.peregruzochka.telegram_bot_backend.service.GroupTimeSlotPatternService;

import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/group_timeslot_patterns")
@RequiredArgsConstructor
public class GroupTimeSlotPattenController {
    private final GroupTimeSlotPatternService groupTimeSlotPatternService;
    private final GroupTimeSlotPatternMapper groupTimeSlotPatternMapper;

    @PostMapping
    GroupTimeSlotPatternDto createTimeSlotPattern(@RequestParam("day") DayOfWeek day,
                                                  @RequestParam("start-time") LocalTime start,
                                                  @RequestParam("teacher-id") UUID teacherId,
                                                  @RequestParam("lesson-id") UUID lessonId) {
        GroupTimeSlotPattern timeSlotPattern = groupTimeSlotPatternService
                .createGroupTimeSlotPattern(day, start, teacherId, lessonId);
        return groupTimeSlotPatternMapper.toTimeSlotPatternDto(timeSlotPattern);
    }
}
