package pl.pwr.footballcompetitionmanager.fragments.leagueupdate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.Mockito
import pl.pwr.footballcompetitionmanager.repository.IRepository

class LeagueUpdateViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: IRepository = Mockito.mock(IRepository::class.java)
    private lateinit var viewModel: LeagueUpdateViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        // Arrange
        viewModel = LeagueUpdateViewModel(repository, 1)

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}