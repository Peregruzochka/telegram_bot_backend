package ru.peregruzochka.telegram_bot_backend.model;

public enum ConfirmStatus {
    CONFIRMED,
    NOT_CONFIRMED,
    FIRST_QUESTION,
    SECOND_QUESTION,
    USER_CANCELLED,
    AUTO_CANCELLED,
}
