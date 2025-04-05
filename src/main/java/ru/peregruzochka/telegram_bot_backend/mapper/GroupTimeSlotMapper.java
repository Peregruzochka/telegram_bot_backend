package ru.peregruzochka.telegram_bot_backend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.peregruzochka.telegram_bot_backend.dto.GroupTimeSlotDto;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;

@Component
@RequiredArgsConstructor
public class GroupTimeSlotMapper {

    private final GroupLessonMapper groupLessonMapper;
    private final TeacherMapper teacherMapper;

    public GroupTimeSlotDto toGroupTimeSlotDto(GroupTimeSlot groupTimeSlot) {
        return GroupTimeSlotDto.builder()
                .id(groupTimeSlot.getId())
                .startTime(groupTimeSlot.getStartTime())
                .endTime(groupTimeSlot.getEndTime())
                .groupLesson(groupLessonMapper.mapToGroupLessonDto(groupTimeSlot.getGroupLesson()))
                .teacher(teacherMapper.toTeacherDto(groupTimeSlot.getTeacher()))
                .registrationAmount(countRegistrations(groupTimeSlot))
                .build();
    }

    private int countRegistrations(GroupTimeSlot groupTimeSlot) {
        if (groupTimeSlot.getRegistrations() == null) {
            return 0;
        }
        return groupTimeSlot.getRegistrations().size();
    }

    public GroupTimeSlot toGroupTimeSlotEntity(GroupTimeSlotDto groupTimeSlotDto) {
        return GroupTimeSlot.builder()
                .id(groupTimeSlotDto.getId())
                .startTime(groupTimeSlotDto.getStartTime())
                .endTime(groupTimeSlotDto.getEndTime())
                .teacher(teacherMapper.toTeacherEntity(groupTimeSlotDto.getTeacher()))
                .build();
    }
}
