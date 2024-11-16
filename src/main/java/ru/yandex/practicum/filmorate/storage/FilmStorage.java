package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    // Добавить новый фильм
    Film addFilm(Film film);
    // Обновить существующий фильм
    Film updateFilm(Film film);
    // Получить фильм по его ID
    Optional<Film> getFilmById(Long id);
    // Удалить фильм по ID
    void deleteFilm(Long id);
    // Получить все фильмы
    List<Film> getAllFilms();
}

