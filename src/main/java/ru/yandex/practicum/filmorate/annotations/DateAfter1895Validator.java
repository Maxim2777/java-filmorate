package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateAfter1895Validator implements ConstraintValidator<DateAfter1895, LocalDate> {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        // Проверяем, что дата не null и позже MIN_DATE
        return date != null && date.isAfter(MIN_DATE);
    }
}
