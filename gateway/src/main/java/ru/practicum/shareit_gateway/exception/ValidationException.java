package ru.practicum.shareit_gateway.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
