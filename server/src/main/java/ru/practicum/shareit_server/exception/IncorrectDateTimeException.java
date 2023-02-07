package ru.practicum.shareit_server.exception;

public class IncorrectDateTimeException extends RuntimeException {

    public IncorrectDateTimeException(String msg) {
        super(msg);
    }

    public IncorrectDateTimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IncorrectDateTimeException(Throwable cause) {
        super(cause);
    }
}
