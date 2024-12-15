package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) { // Указываем, что используется UserDbStorage
        this.userStorage = userStorage;
    }

    // Метод добавления нового пользователя
    public User addUser(User user) {
        log.info("Начало добавления пользователя: {}", user);
        return userStorage.addUser(user);
    }

    // Метод обновления существующего пользователя
    public User updateUser(User user) {
        log.info("Начало обновления пользователя: {}", user);
        return userStorage.updateUser(user);
    }

    // Метод получения пользователя по его идентификатору
    public User getUserById(Long id) {
        log.info("Начало получения пользователя с id: {}", id);
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + id + " не найден"));
    }

    // Метод получения всех пользователей
    public List<User> getAllUsers() {
        log.info("Начало получения всех пользователей");
        return userStorage.getAllUsers();
    }

    public User addFriend(Long userId, Long friendId) {
        log.info("Начало добавления друга с id: {} к пользователю с id: {}", friendId, userId);

        // Проверяем существование пользователей
        userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + friendId + " не найден"));

        log.info("Добавление друга в хранилище");
        if (userStorage instanceof UserDbStorage) {
            // Работа с базой данных
            ((UserDbStorage) userStorage).addFriend(userId, friendId);
        } else {
            // Работа с InMemoryUserStorage
            User user = userStorage.getUserById(userId).orElseThrow();
            user.getFriends().add(friendId);
            User friend = userStorage.getUserById(friendId).orElseThrow();
            friend.getFriends().add(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }

        log.info("Друг с id {} успешно добавлен пользователю с id {}", friendId, userId);
        return userStorage.getUserById(userId).orElseThrow();
    }

    public User removeFriend(Long userId, Long friendId) {
        log.info("Начало удаления друга с id: {} от пользователя с id: {}", friendId, userId);

        // Проверяем существование пользователей
        userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + friendId + " не найден"));

        log.info("Удаление друга из хранилища");
        if (userStorage instanceof UserDbStorage) {
            // Работа с базой данных
            ((UserDbStorage) userStorage).removeFriend(userId, friendId);
        } else {
            // Работа с InMemoryUserStorage
            User user = userStorage.getUserById(userId).orElseThrow();
            user.getFriends().remove(friendId);
            User friend = userStorage.getUserById(friendId).orElseThrow();
            friend.getFriends().remove(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }

        log.info("Друг с id {} успешно удалён у пользователя с id {}", friendId, userId);
        return userStorage.getUserById(userId).orElseThrow();
    }

    // Метод получения списка общих друзей двух пользователей
    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Начало получения общих друзей для пользователей с id: {} и {}", userId, otherId);

        log.info("Проверка наличия первого пользователя с id: {}", userId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Проверка наличия второго пользователя с id: {}", otherId);
        User other = userStorage.getUserById(otherId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + otherId + " не найден"));

        log.info("Нахождение пересечения списков друзей для получения общих друзей");
        user.getFriends().retainAll(other.getFriends());

        log.info("Преобразование списка ID друзей в список объектов User");
        List<User> commonFriends = user.getFriends().stream()
                .map(id -> userStorage.getUserById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + id + " не найден")))
                .toList();

        log.info("Общие друзья для пользователей с id: {} и {}: {}", userId, otherId, commonFriends);
        return commonFriends;
    }
}

