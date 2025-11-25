package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        ValidationException.validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        ValidationException.validateFilm(film);

        if (!filmStorage.findById(film.getId()).isPresent()) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        return filmStorage.update(film);
    }

    public Film getById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);

        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);

        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes == null || !filmLikes.contains(userId)) {
            throw new NotFoundException("Лайк пользователя " + userId + " для фильма " + filmId + " не найден");
        }

        filmLikes.remove(userId);
        log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            count = 10;
        }

        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(
                        likes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                        likes.getOrDefault(f1.getId(), Collections.emptySet()).size()
                ))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmExists(Long filmId) {
        if (!filmStorage.findById(filmId).isPresent()) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    private void validateUserExists(Long userId) {
        if (!userStorage.findById(userId).isPresent()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
