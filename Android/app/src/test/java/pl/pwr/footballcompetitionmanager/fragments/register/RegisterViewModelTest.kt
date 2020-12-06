package pl.pwr.footballcompetitionmanager.fragments.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.Mockito
import pl.pwr.footballcompetitionmanager.fragments.login.LoginViewModel
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository

class RegisterViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: IRepository = Mockito.mock(IRepository::class.java)
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        // Arrange
        viewModel = RegisterViewModel(repository)
    }

    @Test
    fun register_emptyEmailTest() {
        // Act
        viewModel.register("", "username", "password", "password")

        // Assert
        assertNotNull(viewModel.emailFieldError)
    }

    @Test
    fun clearEmailTextFieldErrorTest() {
        // Act
        viewModel.clearEmailTextFieldError()

        // Assert
        assertNull(viewModel.emailFieldError.value)
    }

    @Test
    fun clearUsernameTextFieldErrorTest() {
        // Act
        viewModel.clearUsernameTextFieldError()

        // Assert
        assertNull(viewModel.usernameFieldError.value)
    }

    @Test
    fun clearPasswordTextFieldErrorTest() {
        // Act
        viewModel.clearPasswordTextFieldError()

        // Assert
        assertNull(viewModel.passwordFieldError.value)
    }

    @Test
    fun clearConfirmPasswordTextFieldErrorTest() {
        // Act
        viewModel.clearConfirmPasswordTextFieldError()

        // Assert
        assertNull(viewModel.confirmPasswordFieldError.value)
    }

    @Test
    fun navigationToLoginScreenFinishedTest() {
        // Act
        viewModel.navigationToLoginScreenFinished()

        // Assert
        assertFalse(viewModel.registerSuccessful.value!!)
    }
}