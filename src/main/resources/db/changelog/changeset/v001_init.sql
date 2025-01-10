CREATE TABLE users (
    id              UUID              PRIMARY KEY,
    telegram_id     BIGINT            NOT NULL,
    phone           VARCHAR(16)       NOT NULL,
    user_name       VARCHAR(256)      NOT NULL
);

CREATE TABLE children (
    id              UUID              PRIMARY KEY,
    child_name      VARCHAR(256)      NOT NULL,
    birthday        VARCHAR(32)       NOT NULL,
    parent_id       UUID            NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (parent_id) REFERENCES users (id)
);

CREATE TABLE images (
    id              UUID              PRIMARY KEY,
    image_data      BYTEA             NOT NULL,
    image_name      VARCHAR(32)       NOT NULL,
    image_size      BIGINT            NOT NULL
);

CREATE TABLE teachers (
    id              UUID              PRIMARY KEY,
    name            VARCHAR(256)      NOT NULL,
    image_id        UUID              NOT NULL,

    CONSTRAINT fk_image FOREIGN KEY (image_id) REFERENCES images (id)
);

CREATE TABLE lessons (
    id              UUID              PRIMARY KEY,
    name            VARCHAR(256)      NOT NULL,
    description     TEXT              NOT NULL
);

CREATE TABLE timeslots (
    id              UUID              PRIMARY KEY,
    start_time      TIMESTAMP         NOT NULL,
    end_time        TIMESTAMP         NOT NULL,
    teacher_id      UUID              NOT NULL,

    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);

CREATE TABLE registrations (
    id              UUID              PRIMARY KEY,
    child_id        UUID              NOT NULL,
    lesson_id       UUID              NOT NULL,
    timeslot_id     UUID              NOT NULL,
    confirmed       BOOLEAN           NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_child FOREIGN KEY (child_id) REFERENCES children (id),
    CONSTRAINT fk_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id),
    CONSTRAINT fk_timeslot FOREIGN KEY (timeslot_id) REFERENCES timeslots (id)
);

CREATE TABLE cancel (
    id                  UUID              PRIMARY KEY,
    registration_id     UUID              NOT NULL,
    case_description    TEXT              NOT NULL,
    created_at          TIMESTAMP         NOT NULL,

    CONSTRAINT fk_registration FOREIGN KEY (registration_id) REFERENCES registrations (id)
);
