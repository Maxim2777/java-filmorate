package ru.yandex.practicum.filmorate.storage.db;

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
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sql, this::mapRowToMpaRating);
    }

    @Override
    public Optional<MpaRating> getMpaById(Long id) {
        String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToMpaRating, id).stream().findFirst();
    }

    private MpaRating mapRowToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getLong("mpa_rating_id"));
        mpaRating.setName(rs.getString("name"));
        return mpaRating;
    }
}

