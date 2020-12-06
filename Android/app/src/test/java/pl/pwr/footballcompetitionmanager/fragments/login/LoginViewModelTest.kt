package pl.pwr.footballcompetitionmanager.fragments.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.Mockito
import pl.pwr.footballcompetitionmanager.repository.IRepository

class LoginViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: IRepository = Mockito.mock(IRepository::class.java)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        // Arrange
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun testInit() {
        // Assert
        assertNull(viewModel.usernameFieldError.value)
        assertNull(viewModel.passwordFieldError.value)
        assertFalse(viewModel.loginInProgress.value!!)
        assertFalse(viewModel.loginSuccessful.value!!)
        assertNull(viewModel.loginError.value)
        assertFalse(viewModel.navigateToRegister.value!!)
    }

    @Test
    fun loginButtonClicked_emptyUsernameTest() {
        // Act
        viewModel.loginButtonClicked("", "pass")

        // Assert
        assertFalse(viewModel.loginInProgress.value!!)
        assertNotNull(viewModel.usernameFieldError.value)
    }

    @Test
    fun clearTextFieldErrorsTest() {
        // Act
        viewModel.clearTextFieldErrors()

        // Assert
        assertNull(viewModel.usernameFieldError.value)
        assertNull(viewModel.passwordFieldError.value)
    }

    @Test
    fun loginFinishedTest() {
        // Act
        viewModel.loginFinished()

        // Assert
        assertFalse(viewModel.loginSuccessful.value!!)
    }

    @Test
    fun navigateToRegisterScreenTest() {
        // Act
        viewModel.navigateToRegisterScreen()

        // Assert
        assertTrue(viewModel.navigateToRegister.value!!)
    }

    @Test
    fun navigationToRegisterScreenFinishedTest() {
        // Act
        viewModel.loginFinished()

        // Assert
        assertFalse(viewModel.navigateToRegister.value!!)
    }
}