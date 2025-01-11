package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.TimeSlotDto;
import ru.peregruzochka.telegram_bot_backend.mapper.TimeSlotMapper;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.service.TimeSlotService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/timeslots")
public class TimeSlotController {
    private final TimeSlotMapper timeSlotMapper;
    private final TimeSlotService timeSlotService;

    @GetMapping("/next-month-search")
    public List<TimeSlotDto> getTeacherTimeSlotsInNextMonth(@RequestParam("teacher-id") UUID teacherId) {
        List<TimeSlot> timeSlots = timeSlotService.getTeacherTimeSlotsInNextMonth(teacherId);
        return timeSlotMapper.toTimeSlotDtoList(timeSlots);
    }

    @PostMapping
    public TimeSlotDto addTimeSlot(@RequestParam("teacher-id") UUID teacherId,
                                   @RequestParam("start-time") LocalDateTime startTime,
                                   @RequestParam("end-time") LocalDateTime endTime) {
        TimeSlot timeSlot = timeSlotService.addTimeSlot(teacherId, startTime, endTime);
        return timeSlotMapper.toTimeSlotDto(timeSlot);
    }
}
