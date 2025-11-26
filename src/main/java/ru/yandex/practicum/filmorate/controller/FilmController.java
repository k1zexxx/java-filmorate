package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private static final String LIKE_PATH = "/films/{}/like/{}";

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("GET /films - запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("POST /films - запрос на создание нового фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.debug("PUT /films - запрос на обновление фильма: {}", film);
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.debug("GET /films/{} - запрос на получение фильма по ID", id);
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("PUT " + LIKE_PATH + " - запрос на добавление лайка фильму", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("DELETE " + LIKE_PATH + " - запрос на удаление лайка с фильма", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count) {
        log.debug("GET /films/popular?count={} - запрос на получение популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
