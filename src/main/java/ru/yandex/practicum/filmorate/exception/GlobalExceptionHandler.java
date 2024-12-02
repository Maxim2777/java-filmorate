package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Возвращает объект ErrorResponse и HTTP статус 404 (NOT FOUND).
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException e) {
        log.error("Ошибка ResourceNotFoundException: {}", e.getMessage());
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.NOT_FOUND);
    }

    // Возвращает сообщение об ошибке и HTTP статус 400 (BAD REQUEST).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Ошибка RuntimeException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Обработчик исключения MethodArgumentNotValidException для ошибок валидации аргументов.
    // Возвращает Map с полями и ошибками валидации и HTTP статус 400 (BAD REQUEST).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Создаем Map для хранения ошибок.
        Map<String, String> errors = new HashMap<>();

        // Проходим по всем ошибкам и добавляем их в Map с указанием имени поля и сообщения об ошибке.
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField(); // Имя поля, в котором произошла ошибка.
            String errorMessage = error.getDefaultMessage(); // Сообщение об ошибке.
            errors.put(fieldName, errorMessage);
        });

        log.error("Ошибки валидации: {}", errors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Обработчик всех остальных исключений, которые не были обработаны другими методами.
    // Возвращает сообщение "Internal Server Error" и HTTP статус 500 (INTERNAL SERVER ERROR).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        log.error("Внутренняя ошибка сервера: ", e);
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}







