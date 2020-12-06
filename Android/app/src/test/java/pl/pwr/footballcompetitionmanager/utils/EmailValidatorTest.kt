package pl.pwr.footballcompetitionmanager.utils

import org.junit.Test

import org.junit.Assert.*

class EmailValidatorTest {

    @Test
    fun isValidEmail_correctEmail() {
        // Arrange
        val email = "test@test.com"

        // Act
        val result = EmailValidator.isValidEmail(email)

        // Assert
        assertTrue(result)
    }

    @Test
    fun isValidEmail_emptyString() {
        // Arrange
        val email = ""

        // Act
        val result = EmailValidator.isValidEmail(email)

        // Assert
        assertFalse(result)
    }

    @Test
    fun isValidEmail_noAt() {
        // Arrange
        val email = "mailmail.com"

        // Act
        val result = EmailValidator.isValidEmail(email)

        // Assert
        assertFalse(result)
    }

    @Test
    fun isValidEmail_noDomain() {
        // Arrange
        val email = "mail@mail"

        // Act
        val result = EmailValidator.isValidEmail(email)

        // Assert
        assertFalse(result)
    }

    @Test
    fun isValidEmail_nothingBeforeAt() {
        // Arrange
        val email = "@mail.com"

        // Act
        val result = EmailValidator.isValidEmail(email)

        // Assert
        assertFalse(result)
    }
}