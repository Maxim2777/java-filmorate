package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorResponse = new ErrorResponse(message);
    }

}


