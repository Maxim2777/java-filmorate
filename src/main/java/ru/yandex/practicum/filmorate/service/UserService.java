package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Метод добавления друга.
    public User addFriend(Long userId, Long friendId) {
        // Проверка наличия пользователя (userId).
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        // Проверка наличия друга (friendId).
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + friendId + " не найден"));

        // Добавление друга в список друзей пользователя и наоборот.
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        // Обновление информации о пользователе и друге в хранилище.
        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user;
    }

    // Метод удаления друга.
    public User removeFriend(Long userId, Long friendId) {
        // Проверка наличия пользователя (userId).
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        // Проверка наличия друга (otherId).
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + friendId + " не найден"));

        // Удаление друга из списка друзей пользователя и наоборот.
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        // Обновление информации о пользователе и друге в хранилище.
        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user;
    }

    // Метод получения списка общих друзей двух пользователей.
    public List<User> getCommonFriends(Long userId, Long otherId) {
        // Проверка наличия первого пользователя (userId).
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + userId + " не найден"));

        // Проверка наличия второго пользователя (otherId).
        User other = userStorage.getUserById(otherId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id " + otherId + " не найден"));

        // Нахождение пересечения списков друзей для получения общих друзей.
        user.getFriends().retainAll(other.getFriends());

        // Преобразование списка ID друзей в список объектов User.
        return user.getFriends().stream()
                .map(id -> userStorage.getUserById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Друг с id " + id + " не найден")))
                .toList();
    }
}


