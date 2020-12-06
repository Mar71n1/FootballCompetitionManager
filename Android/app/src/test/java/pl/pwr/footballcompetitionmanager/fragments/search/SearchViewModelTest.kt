package pl.pwr.footballcompetitionmanager.fragments.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import pl.pwr.footballcompetitionmanager.model.Competition
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository

class SearchViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: IRepository = mock(IRepository::class.java)
    private lateinit var viewModel: SearchViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        // Arrange
        viewModel = SearchViewModel(repository)

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun searchTest() = runBlocking {
        // Arrange
        `when`(repository.searchTeamsByName("")).thenReturn(listOf())
        `when`(repository.searchCompetitionsByName("")).thenReturn(listOf())

        // Act
        viewModel.search("search")

        // Assert
        assertThat(viewModel.teams.value, instanceOf(List::class.java))
        assertThat(viewModel.competitions.value, instanceOf(List::class.java))
        assertFalse(viewModel.loading.value!!)
    }
}