package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        // Проверяем MPA и жанры перед добавлением фильма
        validateMpa(film.getMpa());
        validateGenres(film.getGenres());

        // Вставляем основной фильм
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        // Получаем ID добавленного фильма
        String queryForId = "SELECT film_id FROM films WHERE name = ? AND description = ? AND release_date = ? AND duration = ?";
        Long filmId = jdbcTemplate.queryForObject(queryForId, Long.class, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        film.setId(filmId);

        // Добавляем MPA
        addMpaToFilm(film);

        // Добавляем жанры
        addGenresToFilm(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        // Проверяем MPA и жанры перед обновлением фильма
        validateMpa(film.getMpa());
        validateGenres(film.getGenres());

        // Обновляем основной фильм
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());

        // Обновляем MPA
        deleteMpaFromFilm(film.getId());
        addMpaToFilm(film);

        // Обновляем жанры
        deleteGenresFromFilm(film.getId());
        addGenresToFilm(film);

        return film;
    }



    @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, id).stream().findFirst();
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        // Создаём объект Film
        Film film = new Film();

        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // Запрос для получения лайков
        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(likesSql, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));

        // Получение MPA рейтинга
        String mpaSql = "SELECT m.mpa_rating_id, m.name " +
                "FROM mpa_rating m " +
                "JOIN film_mpa_rating fmr ON m.mpa_rating_id = fmr.mpa_rating_id " +
                "WHERE fmr.film_id = ?";
        MpaRating mpaRating = jdbcTemplate.query(mpaSql, this::mapRowToMpaRating, film.getId())
                .stream()
                .findFirst()
                .orElse(null);

        film.setMpa(mpaRating);

        return film;
    }

    private MpaRating mapRowToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getLong("mpa_rating_id"));
        mpaRating.setName(rs.getString("name"));
        return mpaRating;
    }

    private void validateMpa(MpaRating mpa) {
        if (mpa != null && mpa.getId() != null) {
            String checkMpaSql = "SELECT COUNT(*) FROM mpa_rating WHERE mpa_rating_id = ?";
            Integer count = jdbcTemplate.queryForObject(checkMpaSql, Integer.class, mpa.getId());
            if (count == null || count == 0) {
                String errorMessage = "Указанный MPA с id " + mpa.getId() + " не существует";
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    private void validateGenres(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genre.getId() == null) {
                    String errorMessage = "Жанр указан без id";
                    log.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
                String checkGenreSql = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";
                Integer count = jdbcTemplate.queryForObject(checkGenreSql, Integer.class, genre.getId());
                if (count == null || count == 0) {
                    String errorMessage = "Жанр с id " + genre.getId() + " не существует";
                    log.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }
    }

    private void addMpaToFilm(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            String mpaSql = "INSERT INTO film_mpa_rating (film_id, mpa_rating_id) VALUES (?, ?)";
            jdbcTemplate.update(mpaSql, film.getId(), film.getMpa().getId());
        }
    }

    private void deleteMpaFromFilm(Long filmId) {
        String deleteMpaSql = "DELETE FROM film_mpa_rating WHERE film_id = ?";
        jdbcTemplate.update(deleteMpaSql, filmId);
    }


    private void addGenresToFilm(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }
    }

    private void deleteGenresFromFilm(Long filmId) {
        String deleteGenreSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenreSql, filmId);
    }

}


