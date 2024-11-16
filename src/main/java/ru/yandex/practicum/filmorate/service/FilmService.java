package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new RuntimeException("Film not found"));
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new RuntimeException("Film not found"));
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = filmStorage.getAllFilms();
        films.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

}


