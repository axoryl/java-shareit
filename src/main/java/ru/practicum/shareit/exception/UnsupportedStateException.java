package ru.practicum.shareit.exception;

public class UnsupportedStateException extends RuntimeException {

    public UnsupportedStateException(String msg) {
        super(msg);
    }

    public UnsupportedStateException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnsupportedStateException(Throwable cause) {
        super(cause);
    }
}
