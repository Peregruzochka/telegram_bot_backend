CREATE TABLE group_lesson_teacher (
    group_lesson_id UUID NOT NULL,
    teacher_id UUID NOT NULL,

    PRIMARY KEY (group_lesson_id, teacher_id),

    CONSTRAINT fk_group_lesson FOREIGN KEY (group_lesson_id) REFERENCES group_lessons (id),
    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);