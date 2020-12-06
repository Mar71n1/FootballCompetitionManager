package pl.pwr.footballcompetitionmanager.fragments.reportcreate

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
import pl.pwr.footballcompetitionmanager.model.Report
import pl.pwr.footballcompetitionmanager.repository.IRepository

class ReportCreateViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: IRepository
    private lateinit var viewModel: ReportCreateViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        // Arrange
        repository = mock {
            onBlocking { createReport(any()) } doReturn Report("", 1)
        }
        viewModel = ReportCreateViewModel(repository)

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun createReport_correctDescriptionTest() = runBlocking {
        // Arrange
        val description = "aaaaa"

        // Act
        viewModel.createReport(description)

        // Assert
        assertFalse(viewModel.reportCreationSuccessful.value!!)
    }

    @Test
    fun createReport_tooLongDescriptionTest() {
        // Arrange
        val description = "a".repeat(Constants.MAX_REPORT_LENGTH + 1)

        // Act
        viewModel.createReport(description)

        // Assert
        assertNotNull(viewModel.reportCreationErrorMessage.value)
    }

    @Test
    fun navigatedToHomeScreen() {
        // Act
        viewModel.navigatedToHomeScreen()

        // Assert
        assertFalse(viewModel.reportCreationSuccessful.value!!)
    }

    @Test
    fun clearDescriptionTextFieldErrorTest() {
        // Act
        viewModel.clearDescriptionTextFieldError()

        // Assert
        assertNull(viewModel.reportCreationErrorMessage.value)
    }
}