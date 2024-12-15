package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public void validateGenres(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genre.getId() == null) {
                    throw new IllegalArgumentException("Жанр указан без id");
                }
                if (genreStorage.getGenreById(genre.getId()).isEmpty()) {
                    throw new IllegalArgumentException("Жанр с id " + genre.getId() + " не существует");
                }
            }
        }
    }

    public List<Genre> getAllGenres() {
        log.info("Получение всех жанров");
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        log.info("Получение жанра с id: {}", id);
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр с id " + id + " не найден"));
    }
}


