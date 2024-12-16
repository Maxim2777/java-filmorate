package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        log.info("Добавление пользователя: {}", user);
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        String queryForId = "SELECT user_id FROM users WHERE email = ? AND login = ? AND name = ? AND birthday = ?";
        Long userId = jdbcTemplate.queryForObject(queryForId, Long.class, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        user.setId(userId);
        log.info("Пользователь успешно добавлен с ID: {}", userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление пользователя с ID: {}", user.getId());
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        if (rowsUpdated == 0) {
            log.error("Пользователь с ID: {} не найден", user.getId());
            throw new ResourceNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }

        log.info("Пользователь с ID: {} успешно обновлен", user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Получение пользователя с ID: {}", id);
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Optional<User> user = jdbcTemplate.query(sql, this::mapRowToUser, id).stream().findFirst();
        if (user.isPresent()) {
            log.info("Пользователь с ID: {} найден", id);
        } else {
            log.warn("Пользователь с ID: {} не найден", id);
        }
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Пользователь с ID: {} успешно удален", id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление друга с ID: {} для пользователя с ID: {}", friendId, userId);
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Друг с ID: {} успешно добавлен пользователю с ID: {}", friendId, userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление друга с ID: {} для пользователя с ID: {}", friendId, userId);
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Друг с ID: {} успешно удален у пользователя с ID: {}", friendId, userId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        // Создаём объект User
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        // Добавляем друзей
        String friendsSql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        List<Long> friends = jdbcTemplate.queryForList(friendsSql, Long.class, user.getId());
        user.setFriends(new HashSet<>(friends));

        log.debug("Пользователь с ID: {} успешно маппирован", user.getId());
        return user;
    }
}
