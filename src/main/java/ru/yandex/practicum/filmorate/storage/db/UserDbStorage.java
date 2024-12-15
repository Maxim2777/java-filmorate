package ru.yandex.practicum.filmorate.storage.db;

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
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        // Выполняем вставку без RETURNING
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        // Получаем последний сгенерированный ID
        String queryForId = "SELECT user_id FROM users WHERE email = ? AND login = ? AND name = ? AND birthday = ?";
        Long userId = jdbcTemplate.queryForObject(queryForId, Long.class, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        user.setId(userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsUpdated == 0) {
            throw new ResourceNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, id).stream().findFirst();
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }


    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        // Создаём пустой объект User
        User user = new User();

        // Устанавливаем значения через сеттеры
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        // Запрос для получения идентификаторов друзей
        String friendsSql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        List<Long> friends = jdbcTemplate.queryForList(friendsSql, Long.class, user.getId());

        // Добавляем друзей
        user.setFriends(new HashSet<>(friends));

        return user;
    }


}


