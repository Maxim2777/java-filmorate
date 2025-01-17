package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDbStorage.class)
public class GenreDbStorageTests {

    private final GenreDbStorage genreDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM genre");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (1, 'Комедия')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (2, 'Драма')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (3, 'Триллер')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (4, 'Фантастика')");
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (5, 'Документальный')");
    }

    @Test
    public void allGenresRetrievalShouldReturnCompleteList() {
        // Получаем все жанры
        List<Genre> genres = genreDbStorage.getAllGenres();

        // Проверяем размер и содержимое списка
        assertEquals(5, genres.size());
        assertEquals("Комедия", genres.get(0).getName());
        assertEquals("Драма", genres.get(1).getName());
        assertEquals("Триллер", genres.get(2).getName());
        assertEquals("Фантастика", genres.get(3).getName());
        assertEquals("Документальный", genres.get(4).getName());
    }

    @Test
    public void genreRetrievalByIdShouldReturnCorrectGenre() {
        // Получаем жанр по ID
        Optional<Genre> genreOptional = genreDbStorage.getGenreById(3L);

        // Проверяем данные
        assertTrue(genreOptional.isPresent());
        Genre genre = genreOptional.get();
        assertEquals(3L, genre.getId());
        assertEquals("Триллер", genre.getName());
    }

    @Test
    public void genreRetrievalByIdShouldReturnEmptyWhenNotFound() {
        // Попытка получить жанр с несуществующим ID
        Optional<Genre> genreOptional = genreDbStorage.getGenreById(99L);

        // Проверяем, что результат пустой
        assertTrue(genreOptional.isEmpty());
    }
}
