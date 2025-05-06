package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    GroupTimeSlotPatternDto createTimeSlotPattern(@RequestParam("day") DayOfWeek day,
                                                  @RequestParam("start-time") LocalTime start,
                                                  @RequestParam("teacher-id") UUID teacherId,
                                                  @RequestParam("lesson-id") UUID lessonId) {
        GroupTimeSlotPattern timeSlotPattern = groupTimeSlotPatternService
                .createGroupTimeSlotPattern(day, start, teacherId, lessonId);
        return groupTimeSlotPatternMapper.toTimeSlotPatternDto(timeSlotPattern);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTimeSlotPattern(@RequestParam("id") UUID id) {
        groupTimeSlotPatternService.deleteGroupTimeSlotPattern(id);
    }
}
