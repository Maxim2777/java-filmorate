package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user;
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User other = userStorage.getUserById(otherId).orElseThrow(() -> new RuntimeException("Other user not found"));

        Set<Long> commonFriends = user.getFriends();
        commonFriends.retainAll(other.getFriends());

        return commonFriends.stream()
                .map(id -> userStorage.getUserById(id).orElseThrow(() -> new RuntimeException("Friend not found")))
                .collect(Collectors.toList());
    }
}


