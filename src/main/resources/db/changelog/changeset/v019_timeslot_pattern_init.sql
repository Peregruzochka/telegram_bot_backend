CREATE TABLE timeslot_patterns (
    id                  UUID            PRIMARY KEY,
    day_of_week         SMALLINT        NOT NULL,
    start_time          TIME            NOT NULL,
    end_time            TIME            NOT NULL,
    teacher_id          UUID            NOT NULL,

    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);

CREATE TABLE group_timeslot_patterns (
    id                  UUID            PRIMARY KEY,
    day_of_week         SMALLINT        NOT NULL,
    start_time          TIME            NOT NULL,
    end_time            TIME            NOT NULL,
    teacher_id          UUID            NOT NULL,
    group_lesson_id     UUID            NOT NULL,

    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id),
    CONSTRAINT fk_group_lesson FOREIGN KEY (group_lesson_id) REFERENCES group_lessons (id)
);
