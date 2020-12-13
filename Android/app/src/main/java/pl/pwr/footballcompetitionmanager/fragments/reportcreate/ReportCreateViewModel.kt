package pl.pwr.footballcompetitionmanager.fragments.reportcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Report
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent

class ReportCreateViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _reportCreationSuccessful = MutableLiveData<Boolean>(false)
    val reportCreationSuccessful: LiveData<Boolean>
        get() = _reportCreationSuccessful

    private val _reportCreationErrorMessage = MutableLiveData<Int>()
    val reportCreationErrorMessage: LiveData<Int>
        get() = _reportCreationErrorMessage

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    fun createReport(bugDescription: String) {
        if (bugDescription == "") {
            _reportCreationErrorMessage.value = R.string.fragment_report_create_description_message_empty
        } else if (Constants.MAX_REPORT_LENGTH < bugDescription.length) {
            _reportCreationErrorMessage.value = R.string.fragment_report_create_too_long_description_message
        } else {
            viewModelScope.launch {
                try {
                    repository.createReport(Report(bugDescription, repository.getCurrentUser().userId))
                    joinAll()
                    _reportCreationSuccessful.value = true
                } catch (exception: IllegalArgumentException) {
                    _reportCreationErrorMessage.value = R.string.fragment_report_create_too_long_description_message
                } catch (exception: Exception) {
                    _snackbarMessage.value = R.string.server_exception_message
                }
            }
        }
    }

    fun navigatedToHomeScreen() { _reportCreationSuccessful.value = false }

    fun clearDescriptionTextFieldError() { _reportCreationErrorMessage.value = null }
}