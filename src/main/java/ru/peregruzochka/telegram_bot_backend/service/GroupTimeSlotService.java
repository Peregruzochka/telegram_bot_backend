package ru.peregruzochka.telegram_bot_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.telegram_bot_backend.model.GroupLesson;
import ru.peregruzochka.telegram_bot_backend.model.GroupTimeSlot;
import ru.peregruzochka.telegram_bot_backend.model.Teacher;
import ru.peregruzochka.telegram_bot_backend.model.TimeSlot;
import ru.peregruzochka.telegram_bot_backend.repository.GroupLessonRepository;
import ru.peregruzochka.telegram_bot_backend.repository.GroupTimeSlotRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TeacherRepository;
import ru.peregruzochka.telegram_bot_backend.repository.TimeSlotRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupTimeSlotService {
    private final GroupTimeSlotRepository groupTimeSlotRepository;
    private final TeacherRepository teacherRepository;
    private final GroupLessonRepository groupLessonRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public GroupTimeSlot addGroupTimeSlot(UUID teacherId, UUID lessonId, LocalDateTime start) {
        Teacher teacher = getTeacherFromDB(teacherId);
        GroupLesson lesson = getGroupLessonFromDB(lessonId);
        checkTeachersLesson(lesson, teacher);

        checkStartTime(start);

        LocalDateTime end = start.plusMinutes(45);
        checkOverlapping(teacher, start, end);

        GroupTimeSlot groupTimeSlot = GroupTimeSlot.builder()
                .startTime(start)
                .endTime(end)
                .teacher(teacher)
                .groupLesson(lesson)
                .build();

        GroupTimeSlot savedGroupTimeSlot = groupTimeSlotRepository.save(groupTimeSlot);
        log.info("Added group time slot: {}", savedGroupTimeSlot);
        return savedGroupTimeSlot;
    }

    @Transactional(readOnly = true)
    public List<GroupTimeSlot> getTeacherGroupTimeSlotsByDate(UUID teacherId, LocalDate date) {
        Teacher teacher = getTeacherFromDB(teacherId);
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1);
        List<GroupTimeSlot> groupTimeSlots =
                groupTimeSlotRepository.getGroupTimeSlotByTeacherAndStartTimeBetween(teacher, startTime, endTime);
        log.info("Find teacher group time slots by date: {}", groupTimeSlots.size());
        return groupTimeSlots;
    }

    @Transactional(readOnly = true)
    public List<GroupTimeSlot> getTeacherGroupTimeSlotsInNextMonth(UUID teacherId) {
        Teacher teacher = getTeacherFromDB(teacherId);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMonths(1);
        List<GroupTimeSlot> groupTimeSlots =
                groupTimeSlotRepository.getGroupTimeSlotByTeacherAndStartTimeBetween(teacher, startTime, endTime);
        log.info("Find teacher group time slots in next month: {}", groupTimeSlots.size());
        return groupTimeSlots;
    }

    @Transactional
    public void delete(UUID groupTimeslotId) {
        GroupTimeSlot groupTimeSlot = groupTimeSlotRepository.findById(groupTimeslotId).orElseThrow(
                () -> new IllegalArgumentException("GroupTimeSlot not found")
        );

        if (!groupTimeSlot.getRegistrations().isEmpty()) {
            throw new IllegalArgumentException("GroupTimeSlot already has registrations");
        }

        groupTimeSlotRepository.delete(groupTimeSlot);
        log.info("Deleted group time slot: {}", groupTimeSlot);
    }

    private void checkTeachersLesson(GroupLesson lesson, Teacher teacher) {
        if (!lesson.getTeachers().contains(teacher)) {
            throw new IllegalArgumentException("Teacher not in group lesson");
        }
    }

    private void checkOverlapping(Teacher teacher, LocalDateTime start,  LocalDateTime end) {
        List<TimeSlot> individualOverlapping = timeSlotRepository.findOverlappingTimeSlots(teacher, start, end);
        if (!individualOverlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping times slots");
        }

        List<GroupTimeSlot> groupOverlapping = groupTimeSlotRepository.findOverlappingTimeSlots(teacher, start, end);
        if (!groupOverlapping.isEmpty()) {
            throw new IllegalArgumentException("Overlapping times slots");
        }
    }

    private void checkStartTime(LocalDateTime start) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be before now");
        }
    }

    private GroupLesson getGroupLessonFromDB(UUID lessonId) {
        return groupLessonRepository.findById(lessonId).orElseThrow(
                () -> new IllegalArgumentException("Lesson not found")
        );
    }

    private Teacher getTeacherFromDB(UUID teacherId) {
        return teacherRepository.findById(teacherId).orElseThrow(
                () -> new IllegalArgumentException("Teacher not found")
        );
    }


}
