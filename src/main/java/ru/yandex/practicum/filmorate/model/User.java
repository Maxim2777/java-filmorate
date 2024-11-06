package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void validate() {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new IllegalArgumentException("Логин не может быть пустым и не должен содержать пробелы");
        }
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
    }
}
