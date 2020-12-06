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

class ReportCreateViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _reportCreationSuccessful = MutableLiveData<Boolean>(false)
    val reportCreationSuccessful: LiveData<Boolean>
        get() = _reportCreationSuccessful

    private val _reportCreationErrorMessage = MutableLiveData<Int>()
    val reportCreationErrorMessage: LiveData<Int>
        get() = _reportCreationErrorMessage

    fun createReport(bugDescription: String) {
        if (Constants.MAX_REPORT_LENGTH < bugDescription.length)
            _reportCreationErrorMessage.value = R.string.fragment_report_create_too_long_description_message
        
        viewModelScope.launch {
            try {
                repository.createReport(Report(bugDescription, repository.getCurrentUser().userId))
                joinAll()
                _reportCreationSuccessful.value = true
            } catch (exception: IllegalArgumentException) {
                _reportCreationErrorMessage.value = R.string.fragment_report_create_too_long_description_message
            }
        }
    }

    fun navigatedToHomeScreen() { _reportCreationSuccessful.value = false }

    fun clearDescriptionTextFieldError() { _reportCreationErrorMessage.value = null }
}