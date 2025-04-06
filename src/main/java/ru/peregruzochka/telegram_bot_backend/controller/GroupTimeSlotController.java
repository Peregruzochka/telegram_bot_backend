package ru.peregruzochka.telegram_bot_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.telegram_bot_backend.dto.GroupTimeSlotDto;
import ru.peregruzochka.telegram_bot_backend.mapper.GroupTimeSlotMapper;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.service.GroupTimeSlotService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group-timeslots")
public class GroupTimeSlotController {

    private final GroupTimeSlotService groupTimeSlotService;
    private final GroupTimeSlotMapper groupTimeSlotMapper;

    @PostMapping
    public GroupTimeSlotDto addGroupTimeSlot(@RequestParam("teacher-id") UUID teacherId,
                                             @RequestParam("group-lesson-id") UUID groupLessonId,
                                             @RequestParam("start-time") LocalDateTime start) {
        GroupTimeSlot timeslot = groupTimeSlotService.addGroupTimeSlot(teacherId, groupLessonId, start);
        return groupTimeSlotMapper.toGroupTimeSlotDto(timeslot);
    }

    @GetMapping("/by-date")
    public List<GroupTimeSlotDto> getTeacherGroupTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId,
                                                                 @RequestParam("date") LocalDate date) {
        List<GroupTimeSlot> groupTimeSlots = groupTimeSlotService.getTeacherGroupTimeSlotsByDate(teacherId, date);
        return groupTimeSlotMapper.toGroupTimeSlotDtoList(groupTimeSlots);
    }

    @GetMapping("/available-by-date")
    public List<GroupTimeSlotDto> getAvailableGroupTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId,
                                                                   @RequestParam("date") LocalDate date) {
        List<GroupTimeSlot> groupTimeSlots = groupTimeSlotService.getTeacherAvailableGroupTimeSlotsByDate(teacherId, date);
        return groupTimeSlotMapper.toGroupTimeSlotDtoList(groupTimeSlots);
    }

    @GetMapping("/next-month-search")
    public List<GroupTimeSlotDto> getTeacherGroupTimeSlotInNextMonth(@RequestParam("teacher-id") UUID teacherId) {
        List<GroupTimeSlot> groupTimeSlots = groupTimeSlotService.getTeacherGroupTimeSlotsInNextMonth(teacherId);
        return groupTimeSlotMapper.toGroupTimeSlotDtoList(groupTimeSlots);
    }

    @GetMapping("/available-next-month-search")
    public List<GroupTimeSlotDto> getTeacherAvailableGroupTimeSlotInNextMonth(@RequestParam("teacher-id") UUID teacherId) {
        List<GroupTimeSlot> groupTimeSlots = groupTimeSlotService.getTeacherAvailableGroupTimeSlotsInNextMonth(teacherId);
        return groupTimeSlotMapper.toGroupTimeSlotDtoList(groupTimeSlots);
    }

    @GetMapping("/by-user-by-date")
    public List<GroupTimeSlotDto> getUserGroupTimeSlotsByDate(@RequestParam("user-id") UUID userId,
                                                              @RequestParam("date") LocalDate date) {
        List<GroupTimeSlot> groupTimeSlots = groupTimeSlotService.getUserGroupTimeSlotsByDay(userId, date);
        return groupTimeSlotMapper.toGroupTimeSlotDtoList(groupTimeSlots);
    }

    @GetMapping("/{group-timeslot-id}")
    public GroupTimeSlotDto getGroupTimeSlot(@PathVariable("group-timeslot-id") UUID groupTimeslotId) {
        GroupTimeSlot timeSlot = groupTimeSlotService.getGroupTimeSlotById(groupTimeslotId);
        return groupTimeSlotMapper.toGroupTimeSlotDto(timeSlot);
    }

    @DeleteMapping("/{group-timeslot-id}")
    public void deleteGroupTimeSlot(@PathVariable("group-timeslot-id") UUID groupTimeslotId) {
        groupTimeSlotService.delete(groupTimeslotId);
    }
}
