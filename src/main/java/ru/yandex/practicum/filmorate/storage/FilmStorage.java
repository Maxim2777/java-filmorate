package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);               // Добавить новый фильм

    Film updateFilm(Film film);            // Обновить существующий фильм

    Optional<Film> getFilmById(Long id);   // Получить фильм по его ID

    void deleteFilm(Long id);              // Удалить фильм по ID

    List<Film> getAllFilms();              // Получить все фильмы
}

