package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("Попытка добавления фильма: {}", film);
        Film createdFilm = filmService.addFilm(film);
        log.info("Фильм успешно добавлен");
        return ResponseEntity.ok(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Попытка обновления фильма: {}", film);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Фильм успешно обновлен");
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        log.info("Попытка получения фильма с id: {}", id);
        Film film = filmService.getFilmById(id);
        log.info("Фильм успешно получен");
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Попытка получения всех фильмов");
        List<Film> films = filmService.getAllFilms();
        log.info("Все фильмы успешно получены");
        return ResponseEntity.ok(films);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Попытка добавления лайка фильму с id: {} от пользователя с id: {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк успешно добавлен");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Попытка удаления лайка у фильма с id: {} от пользователя с id: {}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Лайк успешно удален");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Попытка получения {} самых популярных фильмов", count);
        List<Film> films = filmService.getMostPopularFilms(count);
        log.info("{} самых популярных фильмов успешно получены", count);
        return ResponseEntity.ok(films);
    }
}