package pl.pwr.footballcompetitionmanager.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.model.*
import pl.pwr.footballcompetitionmanager.repository.IRepository

class HomeViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>>
        get() {
            if (_matches.value == null) {
                _matches.value = listOf()
            }
            return _matches
        }

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

    init {
        _user.value = repository.getCurrentUser()
        viewModelScope.launch {
            _matches.value = repository.getMatchesForUser(user.value!!.userId)
            _teams.value = repository.getTeamsForUser(user.value!!.userId)
            _competitions.value = repository.getCompetitionsForUser(user.value!!.userId)
            joinAll()
            _loading.value = false
        }
    }

    private val _navigateToCreate = MutableLiveData<Boolean>(false)
    val navigateToCreate: LiveData<Boolean>
        get() = _navigateToCreate

    fun refreshData() {
        viewModelScope.launch {
            _matches.value = repository.getMatchesForUser(user.value!!.userId)
            _teams.value = repository.getTeamsForUser(user.value!!.userId)
            _competitions.value = repository.getCompetitionsForUser(user.value!!.userId)
            joinAll()
        }
    }

    fun floatingButtonClicked() {
        _navigateToCreate.value = true
    }

    fun navigatedToCreate() {
        _navigateToCreate.value = false
    }

    fun logout() {
        repository.logout()
    }
}