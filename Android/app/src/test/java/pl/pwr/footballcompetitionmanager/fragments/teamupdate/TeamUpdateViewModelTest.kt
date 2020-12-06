package pl.pwr.footballcompetitionmanager.fragments.teamupdate

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
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository

class TeamUpdateViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: IRepository
    private lateinit var viewModel: TeamUpdateViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() = runBlocking {
        Dispatchers.setMain(Dispatchers.Unconfined)

        // Arrange
        repository = mock {
            onBlocking { getTeam(any()) } doReturn Team(1, "", 1, "", "", "")
        }
        viewModel = TeamUpdateViewModel(repository, 1)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun updateTeam_nameTooShortTest() {
        // Arrange
        val updatedTeamName = "a".repeat(Constants.MIN_TEAM_NAME_LENGTH - 1)
        val description = ""

        // Act
        viewModel.updateTeam(updatedTeamName, description)

        // Assert
        assertEquals(R.string.fragment_team_create_name_length_invalid_error, viewModel.teamNameTextFieldError.value)
    }

    @Test
    fun updateTeam_nameTooLongTest() {
        // Arrange
        val updatedTeamName = "a".repeat(Constants.MAX_TEAM_NAME_LENGTH + 1)
        val description = ""

        // Act
        viewModel.updateTeam(updatedTeamName, description)

        // Assert
        assertEquals(R.string.fragment_team_create_name_length_invalid_error, viewModel.teamNameTextFieldError.value)
    }
}