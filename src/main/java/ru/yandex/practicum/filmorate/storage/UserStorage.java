package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);               // Добавить нового пользователя

    User updateUser(User user);            // Обновить данные пользователя

    Optional<User> getUserById(Long id);   // Получить пользователя по его ID

    void deleteUser(Long id);              // Удалить пользователя по ID

    List<User> getAllUsers();              // Получить всех пользователей
}
