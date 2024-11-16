package ru.yandex.practicum.filmorate.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorResponse = new ErrorResponse(message);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}


