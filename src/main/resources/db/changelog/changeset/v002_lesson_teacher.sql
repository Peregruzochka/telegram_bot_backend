CREATE TABLE lesson_teacher (
    lesson_id UUID NOT NULL,
    teacher_id UUID NOT NULL,

    PRIMARY KEY (lesson_id, teacher_id),

    CONSTRAINT fk_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id),
    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);