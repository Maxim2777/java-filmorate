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
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        log.debug("Инициализация UserService с хранилищем: {}", userStorage.getClass().getSimpleName());
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        log.info("Добавление пользователя: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя: {}", user);
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        log.info("Получение пользователя с ID: {}", id);
        return userStorage.getUserById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        List<User> users = userStorage.getAllUsers();
        log.info("Получено пользователей: {}", users.size());
        return users;
    }

    public User addFriend(Long userId, Long friendId) {
        log.info("Добавление друга с ID: {} пользователю с ID: {}", friendId, userId);

        userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", userId);
                    return new ResourceNotFoundException("Пользователь с id " + userId + " не найден");
                });

        userStorage.getUserById(friendId)
                .orElseThrow(() -> {
                    log.error("Друг с ID {} не найден", friendId);
                    return new ResourceNotFoundException("Пользователь с id " + friendId + " не найден");
                });

        if (userStorage instanceof UserDbStorage) {
            log.debug("Добавление друга в БД");
            ((UserDbStorage) userStorage).addFriend(userId, friendId);
        } else {
            log.debug("Добавление друга в InMemory хранилище");
            User user = userStorage.getUserById(userId).orElseThrow();
            user.getFriends().add(friendId);
            User friend = userStorage.getUserById(friendId).orElseThrow();
            friend.getFriends().add(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }

        log.info("Друг с ID {} успешно добавлен пользователю с ID {}", friendId, userId);
        return userStorage.getUserById(userId).orElseThrow();
    }

    public User removeFriend(Long userId, Long friendId) {
        log.info("Удаление друга с ID: {} у пользователя с ID: {}", friendId, userId);

        userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", userId);
                    return new ResourceNotFoundException("Пользователь с id " + userId + " не найден");
                });

        userStorage.getUserById(friendId)
                .orElseThrow(() -> {
                    log.error("Друг с ID {} не найден", friendId);
                    return new ResourceNotFoundException("Пользователь с id " + friendId + " не найден");
                });

        if (userStorage instanceof UserDbStorage) {
            log.debug("Удаление друга из БД");
            ((UserDbStorage) userStorage).removeFriend(userId, friendId);
        } else {
            log.debug("Удаление друга из InMemory хранилища");
            User user = userStorage.getUserById(userId).orElseThrow();
            user.getFriends().remove(friendId);
            User friend = userStorage.getUserById(friendId).orElseThrow();
            friend.getFriends().remove(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }

        log.info("Друг с ID {} успешно удален у пользователя с ID {}", friendId, userId);
        return userStorage.getUserById(userId).orElseThrow();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получение общих друзей для пользователей с ID: {} и {}", userId, otherId);

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", userId);
                    return new ResourceNotFoundException("Пользователь с id " + userId + " не найден");
                });

        User other = userStorage.getUserById(otherId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", otherId);
                    return new ResourceNotFoundException("Пользователь с id " + otherId + " не найден");
                });

        user.getFriends().retainAll(other.getFriends());
        List<User> commonFriends = user.getFriends().stream()
                .map(id -> userStorage.getUserById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + id + " не найден")))
                .toList();

        log.info("Общие друзья для пользователей с ID: {} и {} найдены: {}", userId, otherId, commonFriends);
        return commonFriends;
    }
}