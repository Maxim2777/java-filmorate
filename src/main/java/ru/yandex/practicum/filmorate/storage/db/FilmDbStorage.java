package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        String[] generatedColumns = {"film_id"}; // Указываем столбец сгенерированного ключа

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, generatedColumns)) {

            // Устанавливаем параметры запроса
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            // Выполняем запрос
            statement.executeUpdate();

            // Получаем сгенерированный идентификатор
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    film.setId(rs.getLong(1)); // Устанавливаем сгенерированный ID в объект Film
                } else {
                    throw new SQLException("Не удалось получить сгенерированный идентификатор для фильма");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении фильма: " + e.getMessage(), e);
        }

        // Добавляем MPA и жанры
        addMpaToFilm(film);
        addGenresToFilm(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());

        deleteMpaFromFilm(film.getId());
        addMpaToFilm(film);

        deleteGenresFromFilm(film.getId());
        addGenresToFilm(film);

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, id).stream().findFirst();
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private void addMpaToFilm(Film film) {
        if (film.getMpa() != null) {
            String sql = "INSERT INTO film_mpa_rating (film_id, mpa_rating_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, film.getId(), film.getMpa().getId());
        }
    }

    private void deleteMpaFromFilm(Long filmId) {
        String sql = "DELETE FROM film_mpa_rating WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void addGenresToFilm(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private void deleteGenresFromFilm(Long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        // Создаём объект Film
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // Получаем лайки
        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(likesSql, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));

        // Получаем MPA
        String mpaSql = "SELECT m.mpa_rating_id, m.name " +
                "FROM mpa_rating m " +
                "JOIN film_mpa_rating fmr ON m.mpa_rating_id = fmr.mpa_rating_id " +
                "WHERE fmr.film_id = ?";
        jdbcTemplate.query(mpaSql, (rsMpa) -> {
            MpaRating mpaRating = new MpaRating();
            mpaRating.setId(rsMpa.getLong("mpa_rating_id"));
            mpaRating.setName(rsMpa.getString("name"));
            film.setMpa(mpaRating);
        }, film.getId());

        // Получаем жанры
        String genresSql = "SELECT g.genre_id, g.name " +
                "FROM genre g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, (rsGenre, rowNumGenre) -> {
            Genre genre = new Genre();
            genre.setId(rsGenre.getLong("genre_id"));
            genre.setName(rsGenre.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new HashSet<>(genres));

        return film;
    }

}