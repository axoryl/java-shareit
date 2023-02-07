package ru.practicum.shareit_server.exception;

public class StatusAlreadySetException extends RuntimeException {

    public StatusAlreadySetException(String msg) {
        super(msg);
    }

    public StatusAlreadySetException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public StatusAlreadySetException(Throwable cause) {
        super(cause);
    }
}
