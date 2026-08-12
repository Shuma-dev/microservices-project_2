package org.example.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    /*
     =====================
     Валидные данные
     =====================
     */

    @Test
    @DisplayName("должен принять корректные данные")
    void shouldValidateCorrectUser() {
        assertDoesNotThrow(() ->
                UserValidator.validate(
                        "Denis",
                        "denis@gmail.com",
                        26));
    }

    /*
     =====================
     Имя
     =====================
     */

    @Test
    @DisplayName("должен выбросить исключение, если имя null")
    void shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        null,
                        "denis@gmail.com",
                        26));
    }

    @Test
    @DisplayName("должен выбросить исключение, если имя пустое")
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        " ",
                        "denis@gmail.com",
                        26));
    }

    /*
     =====================
     Email
     =====================
     */

    @Test
    @DisplayName("должен выбросить исключение, если email null")
    void shouldThrowWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        null,
                        26));
    }

    @Test
    @DisplayName("должен выбросить исключение, если email пустой")
    void shouldThrowWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        " ",
                        26));
    }

    @Test
    @DisplayName("должен выбросить исключение, если email без @")
    void shouldThrowWhenEmailHasNoAt() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "denisgmail.com",
                        26));
    }

    @Test
    @DisplayName("должен выбросить исключение, если email содержит только @")
    void shouldThrowWhenEmailContainsOnlyAt() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "@",
                        26));
    }

    @Test
    @DisplayName("должен выбросить исключение, если email без доменной зоны")
    void shouldThrowWhenEmailHasNoDomainZone() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "denis@gmail",
                        26));
    }

    @Test
    @DisplayName("должен выбросить исключение, если email начинается с @")
    void shouldThrowWhenEmailStartsWithAt() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "@gmail.com",
                        26));
    }

    /*
     =====================
     Возраст
     =====================
     */

    @Test
    @DisplayName("должен выбросить исключение, если возраст null")
    void shouldThrowWhenAgeIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "denis@gmail.com",
                        null));
    }

    @Test
    @DisplayName("должен выбросить исключение, если возраст меньше или равен 0")
    void shouldThrowWhenAgeIsLessThanOne() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "denis@gmail.com",
                        0));
    }

    @Test
    @DisplayName("должен выбросить исключение, если возраст больше 110")
    void shouldThrowWhenAgeIsGreaterThan110() {
        assertThrows(IllegalArgumentException.class,
                () -> UserValidator.validate(
                        "Denis",
                        "denis@gmail.com",
                        111));
    }
}