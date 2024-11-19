package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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

    // Метод добавления друга.
    public User addFriend(Long userId, Long friendId) {
        log.info("Начало добавления друга с id: {} к пользователю с id: {}", friendId, userId);

        log.info("Проверка наличия пользователя с id: {}, к которому хотят добавить друга", userId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Проверка наличия друга с id: {}, которого хотят добавить к пользователю", friendId);
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + friendId + " не найден"));

        log.info("Добавление друга в список друзей пользователя");
        user.getFriends().add(friendId);
        log.info("Добавление пользователя в список друзей друга");
        friend.getFriends().add(userId);

        log.info("Обновление информации о пользователе в хранилище (увеличение числа друзей)");
        userStorage.updateUser(user);
        log.info("Обновление информации о друге в хранилище (увеличение числа друзей)");
        userStorage.updateUser(friend);

        return user;
    }

    // Метод удаления друга.
    public User removeFriend(Long userId, Long friendId) {
        log.info("Начало удаления друга с id: {} от пользователя с id: {}", friendId, userId);

        log.info("Проверка наличия пользователя с id: {}, у которого хотят удалить друга", userId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        log.info("Проверка наличия друга с id: {}, которого хотят удалить у пользователя", friendId);
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + friendId + " не найден"));

        log.info("Удаление друга из списка друзей пользователя");
        user.getFriends().remove(friendId);
        log.info("Удаление пользователя из списка друзей друга");
        friend.getFriends().remove(userId);

        log.info("Обновление информации о пользователе в хранилище (уменьшение числа друзей)");
        userStorage.updateUser(user);
        log.info("Обновление информации о друге в хранилище (уменьшение числа друзей)");
        userStorage.updateUser(friend);

        return user;
    }

    // Метод получения списка общих друзей двух пользователей.
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
