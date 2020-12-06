package pl.pwr.footballcompetitionmanager.fragments.teamcreate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.model.User
import pl.pwr.footballcompetitionmanager.repository.IRepository

class TeamCreateViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repositoryMock: IRepository
    private lateinit var viewModel: TeamCreateViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        // Arrange
//        repository = Mockito.mock(IRepository::class.java)
//        viewModel = TeamCreateViewModel(repository)

        repositoryMock = mock {
            on { getCurrentUser() } doReturn User(1, "", "", "", "", listOf())
            onBlocking { createTeam(any()) } doReturn Team("Team", 1, "")
        }
        viewModel = TeamCreateViewModel(repositoryMock)

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun createNewTeamTest() = runBlocking {
        // Arrange
        viewModel.newTeam.observeForever { }

        // Act
        viewModel.createNewTeam("Nazwa", "")

        // Assert
        assertNotNull(viewModel.getSnackbarMessage())
//        assertNotNull(viewModel.newTeam.value)
    }

    @Test
    fun createNewTeam_nameTooShort() {
        // Arrange
        val newTeamName = "a".repeat(Constants.MIN_TEAM_NAME_LENGTH - 1)
        val description = ""

        // Act
        viewModel.createNewTeam(newTeamName, description)

        // Assert
        assertNotNull(viewModel.teamNameTextFieldError.value)
    }

    @Test
    fun createNewTeam_nameTooLong() {
        // Arrange
        val newTeamName = "a".repeat(Constants.MAX_TEAM_NAME_LENGTH + 1)
        val description = ""

        // Act
        viewModel.createNewTeam(newTeamName, description)

        // Assert
        assertEquals(R.string.fragment_team_create_name_length_invalid_error, viewModel.teamNameTextFieldError.value)
    }

    @Test
    fun createNewTeam_descriptionTooLong() {
        // Arrange
        val newTeamName = "newTeamName"
        val describtion = "a".repeat(Constants.MAX_TEAM_DESCRIPTION_LENGTH + 1)

        // Act
        viewModel.createNewTeam(newTeamName, describtion)

        // Assert
        assertEquals(R.string.fragment_team_create_description_length_invalid_error, viewModel.descriptionTextFieldError.value)
    }

    @Test
    fun newTeamCreatedTest() = runBlocking {
        // Act
        viewModel.createNewTeam("Nazwa", "")
        viewModel.newTeamCreated()

        // Assert
        assertNull(viewModel.newTeam.value)
    }
}