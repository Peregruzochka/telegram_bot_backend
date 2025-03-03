package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.TimeSlotDto;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeSlotMapper {
    private final TeacherMapper teacherMapper;

    public TimeSlotDto toTimeSlotDto(TimeSlot timeSlot) {
        return TimeSlotDto.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .teacherId(timeSlot.getTeacher().getId())
                .available(timeSlot.getIsAvailable())
                .build();
    }

    public List<TimeSlotDto> toTimeSlotDtoList(List<TimeSlot> timeSlots) {
        return timeSlots.stream()
                .map(this::toTimeSlotDto)
                .toList();
    }

    public TimeSlot toTimeSlotEntity(TimeSlotDto timeSlotDto) {
        return TimeSlot.builder()
                .id(timeSlotDto.getId())
                .startTime(timeSlotDto.getStartTime())
                .endTime(timeSlotDto.getEndTime())
                .teacher(Teacher.builder().id(timeSlotDto.getTeacherId()).build())
                .build();
    }
}
