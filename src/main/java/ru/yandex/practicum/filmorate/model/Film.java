package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    //@NotNull
    //@NotBlank
    private String name;
    //@Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    private int duration;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("Максимальная длина описания - 200 символов");
        }
        if (releaseDate == null || releaseDate.isAfter(LocalDate.now()) || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new IllegalArgumentException("Дата релиза должна быть между 28 декабря 1895 года и сегодняшним днём");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Продолжительность фильма должна быть положительным числом");
        }
    }
}