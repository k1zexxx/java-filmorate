package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        ValidationException.validateUser(user);
        User createdUser = userStorage.create(user);
        friends.put(createdUser.getId(), new HashSet<>());
        return createdUser;
    }

    public User update(User user) {
        ValidationException.validateUser(user);
        ValidationException.validateId(user.getId());

        if (!userStorage.findById(user.getId()).isPresent()) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        return userStorage.update(user);
    }

    public User getById(Long id) {
        ValidationException.validateId(id);
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
        }
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        validateUserExists(userId);

        return friends.getOrDefault(userId, new HashSet<>()).stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);

        Set<Long> userFriends = friends.getOrDefault(userId, new HashSet<>());
        Set<Long> otherUserFriends = friends.getOrDefault(otherUserId, new HashSet<>());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Long userId) {
        if (!userStorage.findById(userId).isPresent()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
