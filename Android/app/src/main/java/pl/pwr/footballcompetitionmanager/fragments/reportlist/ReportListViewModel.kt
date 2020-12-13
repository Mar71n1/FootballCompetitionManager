package pl.pwr.footballcompetitionmanager.fragments.reportlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Report
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent

class ReportListViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _unsolvedReports = MutableLiveData<List<Report>>(listOf())
    val unsolvedReports: LiveData<List<Report>>
        get() = _unsolvedReports

    private val _solvedReports = MutableLiveData<List<Report>>()
    val solvedReports: LiveData<List<Report>>
        get() = _solvedReports

    private val _markedAsSolvedMessage = MutableLiveData<Int>()
    val markedAsSolvedMessage : LiveData<Int>
        get() = _markedAsSolvedMessage

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    init {
        viewModelScope.launch {
            try {
                _unsolvedReports.value = repository.getUnsolvedReports()
                _solvedReports.value = repository.getSolvedReports()
                joinAll()
                _loading.value = false
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun markReportAsSolved(reportId: Int) {
        viewModelScope.launch {
            try {
                val isSuccess = repository.markReportAsSolved(reportId)
                joinAll()
                if (isSuccess) {
                    refreshReports()
                    _markedAsSolvedMessage.value = R.string.fragment_report_list_marked_as_solved_snackbar_message
                }
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun refreshReports() {
        viewModelScope.launch {
            try {
                _unsolvedReports.value = repository.getUnsolvedReports()
                _solvedReports.value = repository.getSolvedReports()
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

//    private fun prepareMockData(): MutableList<Report> {
//        val user = User(1, "morzeszko98@gmail.com", "Mar71n1", "pass", LocalDate.now(), listOf(1, 2))
//
//        return mutableListOf(
//            Report(1, "Opis błędu 1", false, user, LocalDate.now(), null),
//            Report(2, "Opis błędu 2", false, user, LocalDate.now(), null),
//            Report(3, "Opis błędu 3", false, user, LocalDate.now(), null),
//            Report(4, "Opis błędu 4", false, user, LocalDate.now(), null),
//            Report(5, "Opis błędu 5", false, user, LocalDate.now(), null),
//            Report(6, "Opis błędu 6", false, user, LocalDate.now(), null),
//            Report(7, "Opis błędu 7", false, user, LocalDate.now(), null),
//            Report(8, "Opis błędu 8", false, user, LocalDate.now(), null),
//            Report(9, "Opis błędu 9", false, user, LocalDate.now(), null),
//            Report(10, "Opis błędu 10", false, user, LocalDate.now(), null),
//            Report(11, "Opis błędu 11", false, user, LocalDate.now(), null),
//            Report(12, "Opis błędu 12", false, user, LocalDate.now(), null),
//            Report(13, "Opis błędu 13", false, user, LocalDate.now(), null),
//            Report(14, "Opis błędu 14", false, user, LocalDate.now(), null),
//            Report(15, "Opis błędu 15", false, user, LocalDate.now(), null),
//            Report(16, "Opis błędu 16", false, user, LocalDate.now(), null),
//            Report(17, "Opis błędu 17", false, user, LocalDate.now(), null),
//            Report(18, "Opis błędu 18", false, user, LocalDate.now(), null),
//            Report(19, "Opis błędu 19", false, user, LocalDate.now(), null),
//            Report(20, "Opis błędu 20", false, user, LocalDate.now(), null)
//        )
//    }
}