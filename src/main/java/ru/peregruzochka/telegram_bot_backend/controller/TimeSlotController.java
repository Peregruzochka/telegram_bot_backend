package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @GetMapping("/available-next-month-search")
    public List<TimeSlotDto> getTeacherTimeSlotsInNextMonth(@RequestParam("teacher-id") UUID teacherId) {
        List<TimeSlot> timeSlots = timeSlotService.getTeacherAvailableTimeSlotsInNextMonth(teacherId);
        return timeSlotMapper.toTimeSlotDtoList(timeSlots);
    }

    @GetMapping("/by-date")
    public List<TimeSlotDto> getTeacherTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId, @RequestParam LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotService.getTeacherTimeSlotsByDate(teacherId, date);
        return timeSlotMapper.toTimeSlotDtoList(timeSlots);
    }

    @GetMapping("/available-by-date")
    public List<TimeSlotDto> getTeacherAvailableTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId, @RequestParam LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotService.getTeacherAvailableTimeSlotsByDate(teacherId, date);
        return timeSlotMapper.toTimeSlotDtoList(timeSlots);
    }

    @PostMapping
    public TimeSlotDto addTimeSlot(@RequestBody TimeSlotDto timeSlotDto) {
        TimeSlot timeSlot = timeSlotMapper.toTimeSlotEntity(timeSlotDto);
        TimeSlot savedTimeSlot = timeSlotService.addTimeSlot(timeSlot);
        return timeSlotMapper.toTimeSlotDto(savedTimeSlot);
    }

    @PostMapping("/fill")
    @ResponseStatus(HttpStatus.CREATED)
    public void fillTimeSlotsByPatterns(@RequestParam("from") LocalDate from,
                                        @RequestParam("to") LocalDate to) {
        timeSlotService.fillByPatterns(from, to);
    }

    @PostMapping("/fill-by-teacher")
    @ResponseStatus(HttpStatus.CREATED)
    public void fillTimeSlotsByPatterns(@RequestParam("teacher-id") UUID teacherId,
                                        @RequestParam("from") LocalDate from,
                                        @RequestParam("to") LocalDate to) {
        timeSlotService.fillByTeacherPatterns(teacherId, from, to);
    }

    @DeleteMapping("/clear-auto-created")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearTimeSlots(@RequestParam("from") LocalDate from,
                               @RequestParam("to") LocalDate to) {
        timeSlotService.clearAutoCreatedSlots(from, to);
    }

    @DeleteMapping("/clear-auto-created-by-teacher")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearTimeSlots(@RequestParam("teacher-id") UUID teacherId,
                               @RequestParam("from") LocalDate from,
                               @RequestParam("to") LocalDate to) {
        timeSlotService.clearTeacherAutoCreatedSlots(teacherId, from, to);
    }

    @DeleteMapping("/{timeslot-id}")
    public void deleteTimeSlot(@PathVariable("timeslot-id") UUID timeslotId) {
        timeSlotService.removeTimeSlot(timeslotId);
    }

    @GetMapping("/{timeslot-id}")
    public TimeSlotDto getTimeSlot(@PathVariable("timeslot-id") UUID timeslotId) {
        TimeSlot timeSlot = timeSlotService.getTimeSlot(timeslotId);
        return timeSlotMapper.toTimeSlotDto(timeSlot);
    }
}
