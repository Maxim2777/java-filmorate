package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Запрос на получение всех жанров");
        String sql = "SELECT * FROM genre";
        List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre);
        log.debug("Найдено жанров: {}", genres.size());
        return genres;
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        log.debug("Запрос на получение жанра по ID: {}", id);
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        Optional<Genre> genre = jdbcTemplate.query(sql, this::mapRowToGenre, id).stream().findFirst();
        if (genre.isPresent()) {
            log.debug("Найден жанр: {}", genre.get());
        } else {
            log.debug("Жанр с ID {} не найден", id);
        }
        return genre;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("name"));
        log.debug("Жанр маппирован: {}", genre);
        return genre;
    }
}