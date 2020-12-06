package pl.pwr.footballcompetitionmanager.fragments.changedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.model.User
import pl.pwr.footballcompetitionmanager.repository.IRepository
import timber.log.Timber

class ChangeDataViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: IRepository = Mockito.mock(IRepository::class.java)
    private lateinit var viewModel: ChangeDataViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        // Arrange
        viewModel = ChangeDataViewModel(repository)
        Mockito.`when`(repository.getCurrentUser()).thenReturn(User(1, "mail@mail.com", "username", "password", "", listOf()))

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun changeData_emptyEmail() {
        // Arrange
        val currentPassword = "password"
        val email = ""

        // Act
        viewModel.changeData(email, "", "", currentPassword)

        // Assert
        assertNotNull(viewModel.emailFieldError.value)
    }

    @Test
    fun changeData_emailNotValid1() {
        // Arrange
        val currentPassword = "password"
        val email = "mail@mail"

        // Act
        viewModel.changeData(email, "", "", currentPassword)

        // Assert
        assertNotNull(viewModel.emailFieldError.value)
    }

    @Test
    fun changeData_emailNotValid2() {
        // Arrange
        val currentPassword = "password"
        val email = "@mail.com"

        // Act
        viewModel.changeData(email, "", "", currentPassword)

        // Assert
        assertNotNull(viewModel.emailFieldError.value)
    }

    @Test
    fun changeData_emailNotValid3() {
        // Arrange
        val currentPassword = "password"
        val email = "mail.mail"

        // Act
        viewModel.changeData(email, "", "", currentPassword)

        // Assert
        assertNotNull(viewModel.emailFieldError.value)
    }

    @Test
    fun changeData_currentPasswordEmpty() {
        // Arrange
        val currentPassword = ""
        val email = "mail@mail.com"

        // Act
        viewModel.changeData(email, "", "", currentPassword)

        // Assert
        assertNotNull(viewModel.currentPasswordFieldError.value)
    }

    @Test
    fun changeData_currentPasswordDoesntMatch() {
        // Arrange
        val currentPassword = "passw0rd"
        val email = "mail@mail.com"

        // Act
        viewModel.changeData(email, "", "", currentPassword)

        // Assert
        assertNotNull(viewModel.currentPasswordFieldError.value)
    }

    @Test
    fun changeData_newPasswordTooShort() {
        // Arrange
        val currentPassword = "password"
        val newPassword = "a".repeat(Constants.MIN_PASSWORD_LENGTH - 1)
        val email = "mail@mail.com"

        // Act
        viewModel.changeData(email, newPassword, newPassword, currentPassword)

        // Assert
        assertNotNull(viewModel.passwordFieldError.value)
    }

    @Test
    fun changeData_newPasswordTooLong() {
        // Arrange
        val currentPassword = "password"
        val newPassword = "a".repeat(Constants.MAX_PASSWORD_LENGTH + 1)
        val email = "mail@mail.com"

        // Act
        viewModel.changeData(email, newPassword, newPassword, currentPassword)

        // Assert
        assertNotNull(viewModel.passwordFieldError.value)
    }

    @Test
    fun changeData_newPasswordsDontMatch() {
        // Arrange
        val currentPassword = "password"
        val newPassword = "passw0rd"
        val newPasswordConfirm = "Passw0rd"
        val email = "mail@mail.com"

        // Act
        viewModel.changeData(email, newPassword, newPasswordConfirm, currentPassword)

        // Assert
        assertNotNull(viewModel.passwordFieldError.value)
        assertNotNull(viewModel.confirmPasswordFieldError.value)
    }
}