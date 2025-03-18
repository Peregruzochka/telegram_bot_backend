CREATE TABLE group_lessons (
    id                  UUID            PRIMARY KEY,
    name                VARCHAR(256)    NOT NULL,
    description         TEXT            NOT NULL,
    group_size          INT             NOT NULL
);

CREATE TABLE group_timeslots (
    id                  UUID            PRIMARY KEY,
    start_time          TIMESTAMP       NOT NULL,
    end_time            TIMESTAMP       NOT NULL,
    teacher_id          UUID            NOT NULL,
    group_lesson_id     UUID            NOT NULL,

    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id),
    CONSTRAINT fk_group_lesson FOREIGN KEY (group_lesson_id) REFERENCES group_lessons (id)
);

CREATE TABLE group_registrations (
    id                  UUID            PRIMARY KEY,
    child_id            UUID            NOT NULL,
    user_id             UUID            NOT NULL,
    group_timeslot_id   UUID,
    registration_type   VARCHAR(64)     NOT NULL,
    confirm_status      VARCHAR(64)     NOT NULL,
    created_at          TIMESTAMP       NOT NULL,

    CONSTRAINT fk_child FOREIGN KEY (child_id) REFERENCES children (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_group_timeslot FOREIGN KEY (group_timeslot_id) REFERENCES group_timeslots (id)
);

CREATE TABLE group_cancel (
    id                      UUID            PRIMARY KEY,
    group_registration_id   UUID            NOT NULL,
    case_description        TEXT            NOT NULL,
    start_time              TIMESTAMP       NOT NULL,
    created_at              TIMESTAMP       NOT NULL,

    CONSTRAINT fk_group_registration FOREIGN KEY (group_registration_id) REFERENCES group_registrations (id)
);


