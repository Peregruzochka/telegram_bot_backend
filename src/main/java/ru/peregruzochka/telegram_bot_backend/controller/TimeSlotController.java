package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.TimeSlotDto;
import ru.peregruzochka.telegram_bot_backend.mapper.TimeSlotMapper;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.service.TimeSlotService;

import java.time.LocalDate;
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

    @GetMapping("/by-date")
    public List<TimeSlotDto> getTeacherTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId, @RequestParam LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotService.getTeacherTimeSlotsByDate(teacherId, date);
        return timeSlotMapper.toTimeSlotDtoList(timeSlots);
    }

    @PostMapping
    public TimeSlotDto addTimeSlot(@RequestBody TimeSlotDto timeSlotDto) {
        TimeSlot timeSlot = timeSlotMapper.toTimeSlotEntity(timeSlotDto);
        TimeSlot savedTimeSlot = timeSlotService.addTimeSlot(timeSlot);
        return timeSlotMapper.toTimeSlotDto(savedTimeSlot);
    }

    @DeleteMapping("/{timeslot-id}")
    public void deleteTimeSlot(@PathVariable("timeslot-id") UUID timeslotId) {
        timeSlotService.removeTimeSlot(timeslotId);
    }
}
