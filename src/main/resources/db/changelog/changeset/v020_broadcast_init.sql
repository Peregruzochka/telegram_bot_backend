CREATE TABLE broadcasts (
    id              UUID        PRIMARY KEY,
    text            TEXT        NOT NULL,
    users_count     BIGINT      NOT NULL,
    read_count      BIGINT      NOT NULL,
    created_at      TIMESTAMP   NOT NULL
);

CREATE TABLE broadcast_deliveries (
    id             UUID       PRIMARY KEY,
    broadcast_id   UUID       NOT NULL,
    user_id        UUID       NOT NULL,
    is_read        BOOLEAN    NOT NULL DEFAULT FALSE,
    last_read_at   TIMESTAMP,

    CONSTRAINT fk_broadcast FOREIGN KEY (broadcast_id) REFERENCES broadcasts (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

