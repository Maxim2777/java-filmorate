package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FilmorateApplicationTests {

    ValidatorFactory factory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void filmValidation_Before1895DateShouldFail() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1800, 1, 1)); // Дата до 1895 года
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Дата должна быть после 28.12.1895"));
    }

    @Test
    void filmValidationEmptyNameShouldFail() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("не может быть пустым"));
    }

    @Test
    void userValidationInvalidEmailShouldFail() {
        User user = new User();
        user.setEmail("invalid-email"); // Неверный формат email
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Неверный формат электронной почты"));
    }

    @Test
    void userValidationBlankLoginShouldFail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(""); // Пустой логин
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("не может быть пустым"));
    }

    @Test
    void userValidationBirthdayInFutureShouldFail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1)); // День рождения в будущем

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Дата рождения не может быть в будущем"));
    }

    @Test
    void filmValidationDescriptionTooLongShouldFail() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("a".repeat(201)); // Описание больше 200 символов
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Максимальная длина описания - 200 символов"));
    }

    @Test
    void filmValidationDurationNotPositiveShouldFail() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10); // Продолжительность не положительное число

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }
}

