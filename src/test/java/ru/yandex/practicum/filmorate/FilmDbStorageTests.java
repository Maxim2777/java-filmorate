package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(FilmDbStorage.class)
public class FilmDbStorageTests {

    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM film_genre");
        jdbcTemplate.execute("DELETE FROM film_mpa_rating");
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films");
    }

    @Test
    public void filmCreationShouldSucceedWhenValidDataProvided() {
        // Создаем фильм
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1L);
        mpaRating.setName("G");
        film.setMpa(mpaRating);


        // Добавляем фильм
        Film savedFilm = filmDbStorage.addFilm(film);

        // Проверяем, что фильм сохранен
        assertNotNull(savedFilm);
        assertEquals(savedFilm.getName(),"Test Film");
    }

    @Test
    public void filmUpdateShouldChangeFieldsCorrectly() {
        // Добавляем фильм в базу
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Old Name", "Old Description", Date.valueOf("2024-01-01"), 90);
        Long filmId = jdbcTemplate.queryForObject("SELECT film_id FROM films WHERE name = 'Old Name'", Long.class);

        // Обновляем фильм
        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("Updated Name");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2024, 6, 1));
        updatedFilm.setDuration(150);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1L);
        mpaRating.setName("G");
        updatedFilm.setMpa(mpaRating);

        Film savedFilm = filmDbStorage.updateFilm(updatedFilm);

        // Проверяем обновленные данные
        assertEquals(savedFilm.getName(),"Updated Name");
        assertEquals(savedFilm.getDescription(),"Updated Description");
        assertEquals(savedFilm.getDuration(), 150);
    }

    @Test
    public void filmRetrievalByIdShouldReturnCorrectFilm() {
        // Добавляем фильм в базу
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Test Film", "Test Description", Date.valueOf("2024-01-01"), 120);
        Long filmId = jdbcTemplate.queryForObject("SELECT film_id FROM films WHERE name = 'Test Film'", Long.class);

        // Получаем фильм
        Optional<Film> filmOptional = filmDbStorage.getFilmById(filmId);

        // Проверяем данные
        assertTrue(filmOptional.isPresent());
        Film film = filmOptional.get();
        assertEquals(film.getName(),"Test Film");
        assertEquals(film.getDescription(), "Test Description");
    }

    @Test
    public void filmDeletionShouldRemoveFilmFromDatabase() {
        // Добавляем фильм в базу
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Test Film", "Test Description", Date.valueOf("2024-01-01"), 120);
        Long filmId = jdbcTemplate.queryForObject("SELECT film_id FROM films WHERE name = 'Test Film'", Long.class);

        // Удаляем фильм
        filmDbStorage.deleteFilm(filmId);

        // Проверяем, что фильм удален
        Optional<Film> filmOptional = filmDbStorage.getFilmById(filmId);
        assertTrue(filmOptional.isEmpty());
    }

    @Test
    public void getAllFilmsShouldReturnAllExistingFilms() {
        // Добавляем фильмы
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Film 1", "Description 1", Date.valueOf("2024-01-01"), 100);
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Film 2", "Description 2", Date.valueOf("2024-02-01"), 120);

        // Получаем все фильмы
        List<Film> films = filmDbStorage.getAllFilms();

        // Проверяем данные
        assertEquals(films.size(), 2);
        assertEquals(films.get(0).getName(), "Film 1");
        assertEquals(films.get(1).getName(),"Film 2");
    }

    @Test
    public void likeChangeShouldChangeLikeCount() {
        // Добавляем фильм и пользователя
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Test Film", "Test Description", Date.valueOf("2024-01-01"), 120);
        Long filmId = jdbcTemplate.queryForObject("SELECT film_id FROM films WHERE name = 'Test Film'", Long.class);
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@example.com", "testuser", "Test User", Date.valueOf("2000-01-01"));
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = 'testuser'", Long.class);

        // Добавляем лайк
        filmDbStorage.addLike(filmId, userId);

        // Проверяем лайк
        Integer likeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId, userId);
        assertEquals(likeCount, 1);

        // Удаляем лайк
        filmDbStorage.removeLike(filmId, userId);

        // Проверяем, что лайк удален
        likeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId, userId);
        assertEquals(likeCount,0);
    }
}

