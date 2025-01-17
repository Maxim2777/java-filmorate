package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
public class UserDbStorageTests {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM friendship");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    public void userAdditionShouldPersistCorrectData() {
        // Создаем пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Добавляем пользователя
        User savedUser = userDbStorage.addUser(user);

        // Проверяем, что пользователь сохранен
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("testuser", savedUser.getLogin());
    }

    @Test
    public void userUpdateShouldModifyExistingData() {
        // Добавляем пользователя в базу
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "old@example.com", "olduser", "Old User", Date.valueOf("1990-01-01"));
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = 'olduser'", Long.class);

        // Обновляем пользователя
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("new@example.com");
        updatedUser.setLogin("newuser");
        updatedUser.setName("New User");
        updatedUser.setBirthday(LocalDate.of(1995, 5, 5));

        User savedUser = userDbStorage.updateUser(updatedUser);

        // Проверяем обновленные данные
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("newuser", savedUser.getLogin());
        assertEquals("New User", savedUser.getName());
    }

    @Test
    public void userRetrievalByIdShouldReturnCorrectUser() {
        // Добавляем пользователя в базу
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@example.com", "testuser", "Test User", Date.valueOf("2000-01-01"));
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = 'testuser'", Long.class);

        // Получаем пользователя
        Optional<User> userOptional = userDbStorage.getUserById(userId);

        // Проверяем данные
        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getLogin());
    }

    @Test
    public void userDeletionShouldRemoveUser() {
        // Добавляем пользователя в базу
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@example.com", "testuser", "Test User", Date.valueOf("2000-01-01"));
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = 'testuser'", Long.class);

        // Удаляем пользователя
        userDbStorage.deleteUser(userId);

        // Проверяем, что пользователь удален
        Optional<User> userOptional = userDbStorage.getUserById(userId);
        assertTrue(userOptional.isEmpty());
    }

    @Test
    public void allUsersRetrievalShouldReturnCompleteList() {
        // Добавляем пользователей
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user1@example.com", "user1", "User 1", Date.valueOf("2000-01-01"));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user2@example.com", "user2", "User 2", Date.valueOf("1995-05-05"));

        // Получаем всех пользователей
        List<User> users = userDbStorage.getAllUsers();

        // Проверяем данные
        assertEquals(2, users.size());
        assertEquals("user1@example.com", users.get(0).getEmail());
        assertEquals("user2@example.com", users.get(1).getEmail());
    }

    @Test
    public void friendChangeShouldChangeFriendshipRecord() {
        // Добавляем пользователей
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user1@example.com", "user1", "User 1", Date.valueOf("2000-01-01"));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user2@example.com", "user2", "User 2", Date.valueOf("1995-05-05"));

        Long userId1 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = 'user1'", Long.class);
        Long userId2 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = 'user2'", Long.class);

        // Добавляем друга
        userDbStorage.addFriend(userId1, userId2);

        // Проверяем, что друг добавлен
        Integer friendCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?",
                Integer.class, userId1, userId2);
        assertEquals(1, friendCount);

        // Удаляем друга
        userDbStorage.removeFriend(userId1, userId2);

        // Проверяем, что друг удален
        friendCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?",
                Integer.class, userId1, userId2);
        assertEquals(0, friendCount);
    }
}

