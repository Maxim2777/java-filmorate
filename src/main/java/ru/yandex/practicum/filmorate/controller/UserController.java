package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Попытка создания пользователя: {}", user);
        User createdUser = userService.addUser(user);
        log.info("Пользователь успешно создан");
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Попытка обновления пользователя: {}", user);
        User updatedUser = userService.updateUser(user);
        log.info("Пользователь успешно обновлен");
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Попытка получения пользователя с id: {}", id);
        User user = userService.getUserById(id);
        log.info("Пользователь успешно получен");
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Попытка получения всех пользователей");
        List<User> users = userService.getAllUsers();
        log.info("Все пользователи успешно получены");
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Попытка добавления друга с id: {} к пользователю с id: {}", friendId, id);
        User user = userService.addFriend(id, friendId);
        log.info("Друг успешно добавлен");
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Попытка удаления друга с id: {} от пользователя с id: {}", friendId, id);
        User user = userService.removeFriend(id, friendId);
        log.info("Друг успешно удален");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getUserFriends(@PathVariable Long id) {
        log.info("Попытка получения друзей пользователя с id: {}", id);
        User user = userService.getUserById(id);
        List<User> friends = user.getFriends().stream()
                .map(userService::getUserById)
                .toList();
        log.info("Друзья пользователя успешно получены");
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Попытка получения общих друзей для пользователей с id: {} и {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Общие друзья пользователей успешно получены");
        return ResponseEntity.ok(commonFriends);
    }
}