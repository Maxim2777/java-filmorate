package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        log.debug("Запрос на получение всех MPA рейтингов");
        String sql = "SELECT * FROM mpa_rating";
        List<MpaRating> mpaRatings = jdbcTemplate.query(sql, this::mapRowToMpaRating);
        log.debug("Найдено MPA рейтингов: {}", mpaRatings.size());
        return mpaRatings;
    }

    @Override
    public Optional<MpaRating> getMpaById(Long id) {
        log.debug("Запрос на получение MPA рейтинга по ID: {}", id);
        String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
        Optional<MpaRating> mpaRating = jdbcTemplate.query(sql, this::mapRowToMpaRating, id).stream().findFirst();
        if (mpaRating.isPresent()) {
            log.debug("Найден MPA рейтинг: {}", mpaRating.get());
        } else {
            log.debug("MPA рейтинг с ID {} не найден", id);
        }
        return mpaRating;
    }

    private MpaRating mapRowToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getLong("mpa_rating_id"));
        mpaRating.setName(rs.getString("name"));
        log.debug("MPA рейтинг маппирован: {}", mpaRating);
        return mpaRating;
    }
}