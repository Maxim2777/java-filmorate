package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        log.info("Начало добавления фильма: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Начало обновления фильма: {}", film);
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.error("Ошибка обновления. Фильм с id {} не найден", film.getId());
            throw new ResourceNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        log.info("Начало получения фильма с id: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с id " + id + " не найден"));
    }

    public List<Film> getAllFilms() {
        log.info("Начало получения всех фильмов");
        return filmStorage.getAllFilms();
    }

    // Добавление лайка фильму
    public void addLike(Long filmId, Long userId) {
        log.info("Проверка наличия фильма, для добавления лайка");
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с id " + filmId + " не найден"));

        log.info("Проверка наличия пользователя, для добавления лайка");
        userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Добавление лайка");
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь с id {} добавил лайк фильму с id {}", userId, filmId);
    }

    // Удаление лайка у фильма
    public void removeLike(Long filmId, Long userId) {
        log.info("Проверка наличия фильма, для удаления лайка");
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с id " + filmId + " не найден"));

        log.info("Проверка наличия пользователя, для удаления лайка");
        userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Удаление лайка");
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
    }

    // Получение списка самых популярных фильмов
    public List<Film> getMostPopularFilms(int count) {
        log.info("Начало получения {} самых популярных фильмов", count);
        log.info("Получение всех фильмов");
        List<Film> films = filmStorage.getAllFilms();
        log.info("Сортировка фильмов по популярности");
        films.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        log.info("Отделение {} самых популярных", count);
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}