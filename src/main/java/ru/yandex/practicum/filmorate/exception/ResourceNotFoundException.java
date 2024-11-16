package ru.yandex.practicum.filmorate.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final Object body;

    public ResourceNotFoundException(Object body) {
        super();
        this.body = body;
    }

    public Object getBody() {
        return body;
    }
}

