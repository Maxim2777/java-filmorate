package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        User createdUser = userStorage.addUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        User updatedUser = userStorage.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Получение пользователя с id: {}", id);
        return userStorage.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Получение всех пользователей");
        List<User> users = userStorage.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление друга с id: {} к пользователю с id: {}", friendId, id);
        User user = userService.addFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаление друга с id: {} от пользователя с id: {}", friendId, id);
        User user = userService.removeFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getUserFriends(@PathVariable Long id) {
        log.info("Получение друзей пользователя с id: {}", id);
        User user = userStorage.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<User> friends = user.getFriends().stream()
                .map(friendId -> userStorage.getUserById(friendId).orElseThrow(() -> new RuntimeException("Friend not found")))
                .toList();
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получение общих друзей для пользователей с id: {} и {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}
