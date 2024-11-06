package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextFilmId = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        film.validate();
        film.setId(nextFilmId);
        log.info("Добавление фильма: {}", film);
        films.put(film.getId(), film);
        nextFilmId++;
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        film.validate();
        log.info("Обновление фильма: {}", film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new IllegalArgumentException("Фильм с таким id не найден");
        }
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return films.values().stream().collect(Collectors.toList());
    }
}
