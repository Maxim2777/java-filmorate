package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @NotNull(message = "Электронная почта не может быть null")
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Неверный формат электронной почты")
    private String email;
    @NotNull(message = "Логин не может быть null")
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть null")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    // Поле для хранения друзей
    private Set<Long> friends = new HashSet<>(); // Инициализируем по умолчанию
}
