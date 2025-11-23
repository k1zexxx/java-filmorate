package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private Film validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate("2000-01-01");
        validFilm.setDuration(120);
    }

    @Test
    void validateFilm_WithValidData_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationException.validateFilm(validFilm));
    }

    @Test
    void validateFilm_WithNullName_ShouldThrowValidationException() {
        validFilm.setName(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void validateFilm_WithEmptyName_ShouldThrowValidationException() {
        validFilm.setName("   ");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void validateFilm_WithBlankName_ShouldThrowValidationException() {
        validFilm.setName("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void validateFilm_WithDescriptionOver200Chars_ShouldThrowValidationException() {
        String longDescription = "A".repeat(201);
        validFilm.setDescription(longDescription);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void validateFilm_WithDescriptionExactly200Chars_ShouldNotThrowException() {
        String exactDescription = "A".repeat(200);
        validFilm.setDescription(exactDescription);

        assertDoesNotThrow(() -> ValidationException.validateFilm(validFilm));
    }

    @Test
    void validateFilm_WithNullReleaseDate_ShouldThrowValidationException() {
        validFilm.setReleaseDate(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Дата релиза обязательна", exception.getMessage());
    }

    @Test
    void validateFilm_WithReleaseDateBefore1895_12_28_ShouldThrowValidationException() {
        validFilm.setReleaseDate("1895-12-27");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void validateFilm_WithReleaseDateExactly1895_12_28_ShouldNotThrowException() {
        validFilm.setReleaseDate("1895-12-28");

        assertDoesNotThrow(() -> ValidationException.validateFilm(validFilm));
    }

    @Test
    void validateFilm_WithNullDuration_ShouldThrowValidationException() {
        validFilm.setDuration(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void validateFilm_WithZeroDuration_ShouldThrowValidationException() {
        validFilm.setDuration(0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void validateFilm_WithNegativeDuration_ShouldThrowValidationException() {
        validFilm.setDuration(-10);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(validFilm));

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void validateFilm_WithPositiveDuration_ShouldNotThrowException() {
        validFilm.setDuration(1);
        assertDoesNotThrow(() -> ValidationException.validateFilm(validFilm));

        validFilm.setDuration(999);
        assertDoesNotThrow(() -> ValidationException.validateFilm(validFilm));
    }

    @Test
    void validateFilm_BoundaryConditions_AllFieldsInvalid() {
        Film invalidFilm = new Film();
        invalidFilm.setName("");
        invalidFilm.setDescription("A".repeat(201));
        invalidFilm.setReleaseDate(String.valueOf(LocalDate.of(1890, 1, 1).atStartOfDay()));
        invalidFilm.setDuration(-10);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateFilm(invalidFilm));

        // Проверяем, что выбрасывается первая найденная ошибка (название)
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }
}
