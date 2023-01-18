package ru.practicum.shareit.exception;

public class UnavailableException extends RuntimeException {

    public UnavailableException(String msg) {
        super(msg);
    }

    public UnavailableException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnavailableException(Throwable cause) {
        super(cause);
    }
}
