package ru.practicum.shareit_server.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String msg) {
        super(msg);
    }

    public AccessDeniedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }
}
