package ru.practicum.shareit_server.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }

    public AlreadyExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
