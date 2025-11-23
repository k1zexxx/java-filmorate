package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        film.setId(getNextId());
        ValidationException.validateFilm(film);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            ValidationException.validateFilm(newFilm);
            films.put(newFilm.getId(), newFilm);
            log.info("Обновлен фильм: {}", newFilm);
            return newFilm;
        }
        log.warn("Попытка обновления несуществующего фильма с id: {}", newFilm.getId());
        throw new ValidationException(String.format("Фильм с id %d не найден", newFilm.getId()));
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
