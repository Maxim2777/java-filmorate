package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(MpaDbStorage.class)
public class MpaDbStorageTests {

    private final MpaDbStorage mpaDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM mpa_rating");
        jdbcTemplate.update("INSERT INTO mpa_rating (mpa_rating_id, name) VALUES (1, 'G')");
        jdbcTemplate.update("INSERT INTO mpa_rating (mpa_rating_id, name) VALUES (2, 'PG')");
        jdbcTemplate.update("INSERT INTO mpa_rating (mpa_rating_id, name) VALUES (3, 'PG-13')");
        jdbcTemplate.update("INSERT INTO mpa_rating (mpa_rating_id, name) VALUES (4, 'R')");
        jdbcTemplate.update("INSERT INTO mpa_rating (mpa_rating_id, name) VALUES (5, 'NC-17')");
    }

    @Test
    public void allMpaRatingsRetrievalShouldReturnCompleteList() {
        // Получаем все MPA рейтинги
        List<MpaRating> mpaRatings = mpaDbStorage.getAllMpa();

        // Проверяем размер и содержимое списка
        assertEquals(5, mpaRatings.size());
        assertEquals("G", mpaRatings.get(0).getName());
        assertEquals("PG", mpaRatings.get(1).getName());
        assertEquals("PG-13", mpaRatings.get(2).getName());
        assertEquals("R", mpaRatings.get(3).getName());
        assertEquals("NC-17", mpaRatings.get(4).getName());
    }

    @Test
    public void mpaRatingRetrievalByIdShouldReturnCorrectRating() {
        // Получаем MPA рейтинг по ID
        Optional<MpaRating> mpaRatingOptional = mpaDbStorage.getMpaById(3L);

        // Проверяем данные
        assertTrue(mpaRatingOptional.isPresent());
        MpaRating mpaRating = mpaRatingOptional.get();
        assertEquals(3L, mpaRating.getId());
        assertEquals("PG-13", mpaRating.getName());
    }

    @Test
    public void mpaRatingRetrievalByIdShouldReturnEmptyWhenNotFound() {
        // Попытка получить MPA рейтинг с несуществующим ID
        Optional<MpaRating> mpaRatingOptional = mpaDbStorage.getMpaById(99L);

        // Проверяем, что результат пустой
        assertTrue(mpaRatingOptional.isEmpty());
    }
}

