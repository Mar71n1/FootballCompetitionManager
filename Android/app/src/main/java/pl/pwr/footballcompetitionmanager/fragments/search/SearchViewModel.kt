package pl.pwr.footballcompetitionmanager.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Competition
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent

class SearchViewModel(private val repository: IRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() {
            if (_teams.value == null) {
                _teams.value = listOf()
            }
            return _teams
        }

    private val _competitions = MutableLiveData<List<Competition>>()
    val competitions: LiveData<List<Competition>>
        get() {
            if (_competitions.value == null) {
                _competitions.value = listOf()
            }
            return _competitions
        }

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    fun search(searchString: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _teams.value = repository.searchTeamsByName(searchString)
                _competitions.value = repository.searchCompetitionsByName(searchString)
                joinAll()
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            } finally {
                _loading.value = false
            }
        }
    }

    fun getCurrentUserId() = repository.getCurrentUser().userId
}