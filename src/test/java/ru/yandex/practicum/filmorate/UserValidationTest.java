package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("validlogin");
        validUser.setName("Valid Name");
        validUser.setBirthday(String.valueOf(LocalDate.of(1990, 1, 1).atStartOfDay()));
    }

    @Test
    void validateUser_WithValidData_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
    }

    @Test
    void validateUser_WithNullEmail_ShouldThrowValidationException() {
        validUser.setEmail(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Электронная почта не может быть пустой", exception.getMessage());
    }

    @Test
    void validateUser_WithEmptyEmail_ShouldThrowValidationException() {
        validUser.setEmail("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Электронная почта не может быть пустой", exception.getMessage());
    }

    @Test
    void validateUser_WithBlankEmail_ShouldThrowValidationException() {
        validUser.setEmail("   ");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Электронная почта не может быть пустой", exception.getMessage());
    }

    @Test
    void validateUser_WithEmailWithoutAtSymbol_ShouldThrowValidationException() {
        validUser.setEmail("invalid-email");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Электронная почта должна содержать символ @", exception.getMessage());
    }

    @Test
    void validateUser_WithValidEmailFormats_ShouldNotThrowException() {
        String[] validEmails = {
                "user@example.com",
                "user.name@example.com",
                "user@sub.example.com",
                "user+tag@example.com"
        };

        for (String email : validEmails) {
            validUser.setEmail(email);
            assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
        }
    }

    @Test
    void validateUser_WithNullLogin_ShouldThrowValidationException() {
        validUser.setLogin(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void validateUser_WithEmptyLogin_ShouldThrowValidationException() {
        validUser.setLogin("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void validateUser_WithBlankLogin_ShouldThrowValidationException() {
        validUser.setLogin("   ");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void validateUser_WithLoginContainingSpaces_ShouldThrowValidationException() {
        String[] invalidLogins = {
                "login with spaces",
                " login",
                "login ",
                "log in"
        };

        for (String login : invalidLogins) {
            validUser.setLogin(login);
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> ValidationException.validateUser(validUser));
            assertEquals("Логин не может содержать пробелы", exception.getMessage());
        }
    }

    @Test
    void validateUser_WithValidLogins_ShouldNotThrowException() {
        String[] validLogins = {
                "login",
                "user123",
                "user_name",
                "User-Name",
                "a"
        };

        for (String login : validLogins) {
            validUser.setLogin(login);
            assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
        }
    }

    @Test
    void validateUser_WithNullName_ShouldSetNameFromLogin() {
        validUser.setName(null);

        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
        assertEquals("validlogin", validUser.getName());
    }

    @Test
    void validateUser_WithEmptyName_ShouldSetNameFromLogin() {
        validUser.setName("");

        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
        assertEquals("validlogin", validUser.getName());
    }

    @Test
    void validateUser_WithBlankName_ShouldSetNameFromLogin() {
        validUser.setName("   ");

        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
        assertEquals("validlogin", validUser.getName());
    }

    /*@Test
    void validateUser_WithFutureBirthday_ShouldThrowValidationException() {
        validUser.setBirthday(String.valueOf(LocalDate.now().plusDays(1).atStartOfDay()));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(validUser));

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }*/

    @Test
    void validateUser_WithTodayBirthday_ShouldNotThrowException() {
        validUser.setBirthday(String.valueOf(LocalDate.now().atStartOfDay()));

        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
    }

    @Test
    void validateUser_WithPastBirthday_ShouldNotThrowException() {
        validUser.setBirthday(String.valueOf(LocalDate.now().minusDays(1).atStartOfDay()));
        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));

        validUser.setBirthday(String.valueOf(LocalDate.of(1900, 1, 1).atStartOfDay()));
        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
    }

    @Test
    void validateUser_WithNullBirthday_ShouldNotThrowException() {
        validUser.setBirthday(null);

        assertDoesNotThrow(() -> ValidationException.validateUser(validUser));
    }

    @Test
    void validateUser_BoundaryConditions_AllFieldsInvalid() {
        User invalidUser = new User();
        invalidUser.setEmail("invalid");
        invalidUser.setLogin("invalid login");
        invalidUser.setBirthday(String.valueOf(LocalDate.now().plusYears(1).atStartOfDay()));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationException.validateUser(invalidUser));

        // Проверяем, что выбрасывается первая найденная ошибка (email)
        assertEquals("Электронная почта должна содержать символ @", exception.getMessage());
    }
}
