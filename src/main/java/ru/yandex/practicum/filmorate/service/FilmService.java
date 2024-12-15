package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaService mpaService,
            GenreService genreService
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film addFilm(Film film) {
        log.info("Начало добавления фильма: {}", film);

        // Проверяем MPA и жанры перед добавлением фильма
        mpaService.validateMpa(film.getMpa());
        genreService.validateGenres(film.getGenres());

        // Добавляем фильм в хранилище
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Фильм добавлен успешно: {}", addedFilm);

        return addedFilm;
    }

    public Film updateFilm(Film film) {
        log.info("Начало обновления фильма: {}", film);

        // Проверяем, существует ли фильм
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            throw new ResourceNotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        // Проверяем MPA и жанры перед обновлением фильма
        mpaService.validateMpa(film.getMpa());
        genreService.validateGenres(film.getGenres());

        // Обновляем фильм в хранилище
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Фильм обновлен успешно: {}", updatedFilm);

        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        log.info("Получение фильма с id: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с id " + id + " не найден"));
    }

    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Проверка наличия фильма для добавления лайка");
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с id " + filmId + " не найден"));

        log.info("Проверка наличия пользователя для добавления лайка");
        userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Добавление лайка");
        if (filmStorage instanceof FilmDbStorage) {
            // Если используется FilmDbStorage, обновляем таблицу likes
            ((FilmDbStorage) filmStorage).addLike(filmId, userId);
        } else {
            // Если используется InMemoryFilmStorage, обновляем объект Film
            film.getLikes().add(userId);
            filmStorage.updateFilm(film);
        }

        log.info("Пользователь с id {} добавил лайк фильму с id {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Проверка наличия фильма для удаления лайка");
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм с id " + filmId + " не найден"));

        log.info("Проверка наличия пользователя для удаления лайка");
        userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Удаление лайка");
        if (filmStorage instanceof FilmDbStorage) {
            // Если используется FilmDbStorage, удаляем запись из таблицы likes
            ((FilmDbStorage) filmStorage).removeLike(filmId, userId);
        } else {
            // Если используется InMemoryFilmStorage, обновляем объект Film
            film.getLikes().remove(userId);
            filmStorage.updateFilm(film);
        }

        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        log.info("Начало получения {} самых популярных фильмов", count);
        List<Film> films = filmStorage.getAllFilms();

        log.info("Сортировка фильмов по количеству лайков");
        films.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));

        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
