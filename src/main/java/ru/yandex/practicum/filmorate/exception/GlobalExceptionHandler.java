package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработчик исключения ResourceNotFoundException.
    // Возвращает объект ErrorResponse и HTTP статус 404 (NOT FOUND).
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.NOT_FOUND);
    }

    // Обработчик исключения RuntimeException.
    // Возвращает сообщение об ошибке и HTTP статус 400 (BAD REQUEST).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Обработчик исключения MethodArgumentNotValidException для ошибок валидации аргументов.
    // Возвращает карту с полями и ошибками валидации и HTTP статус 400 (BAD REQUEST).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Создаем карту для хранения ошибок.
        Map<String, String> errors = new HashMap<>();

        // Проходим по всем ошибкам и добавляем их в карту с указанием имени поля и сообщения об ошибке.
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField(); // Имя поля, в котором произошла ошибка.
            String errorMessage = error.getDefaultMessage(); // Сообщение об ошибке.
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Обработчик всех остальных исключений, которые не были обработаны другими методами.
    // Возвращает сообщение "Internal Server Error" и HTTP статус 500 (INTERNAL SERVER ERROR).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}






