package pl.pwr.footballcompetitionmanager.fragments.teamdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.*
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent
import timber.log.Timber

class TeamDetailViewModel(
    private val repository: IRepository,
    teamId: Int
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _teamReady = MutableLiveData<Boolean>(false)
    val teamReady: LiveData<Boolean>
        get() = _teamReady

    private val _team = MutableLiveData<Team>()
    val team: LiveData<Team>
        get() = _team

    private val _currentUserStatus = MutableLiveData<RequestStatus>()
    val currentUserStatus: LiveData<RequestStatus>
        get() = _currentUserStatus

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>>
        get() = _matches

    private val _results = MutableLiveData<List<Match>>()
    val results: LiveData<List<Match>>
        get() = _results

    private val _competitions = MutableLiveData<List<Competition>>()
    val competitions: LiveData<List<Competition>>
        get() = _competitions

    private val _players = MutableLiveData<List<User>>()
    val players: LiveData<List<User>>
        get() = _players

    private val _requestsUsers = MutableLiveData<List<User>>()
    val requestsUsers: LiveData<List<User>>
        get() = _requestsUsers

    private val _currentUserOwnedTeams = MutableLiveData<List<Team>>()
    val currentUserOwnedTeams: LiveData<List<Team>>
        get() {
            if (_currentUserOwnedTeams.value == null) {
                _currentUserOwnedTeams.value = listOf()
            }
            return _currentUserOwnedTeams
        }

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage
//    val snackbarMessage: LiveData<Int>
//        get() = _snackbarMessage

    init {
        viewModelScope.launch {
            try {
                _team.value = repository.getTeam(teamId)
                joinAll()
                _teamReady.value = true
                setCurrentUserStatus(repository.getActualRequestStatus(team.value!!.teamId!!, repository.getCurrentUser().userId))
                _matches.value = repository.getIncomingMatchesByTeam(team.value!!.teamId!!)
                _results.value = repository.getLatestResultsByTeam(team.value!!.teamId!!)
                _competitions.value = repository.getCompetitionsByTeam(team.value!!.teamId!!)
                _players.value = repository.getPlayersByTeam(team.value!!.teamId!!)
                if (isCurrentUserOwner())
                    _requestsUsers.value = repository.getPendingRequestsUsersForTeam(team.value!!.teamId!!)
                else
                    _currentUserOwnedTeams.value = repository.getOwnerTeams(repository.getCurrentUser().userId)
                joinAll()
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            } finally {
                _loading.value = false
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                _team.value = repository.getTeam(team.value!!.teamId!!)
                joinAll()
                _matches.value = repository.getIncomingMatchesByTeam(team.value!!.teamId!!)
                _results.value = repository.getLatestResultsByTeam(team.value!!.teamId!!)
                _competitions.value = repository.getCompetitionsByTeam(team.value!!.teamId!!)
                _players.value = repository.getPlayersByTeam(team.value!!.teamId!!)
                if (isCurrentUserOwner())
                    _requestsUsers.value = repository.getPendingRequestsUsersForTeam(team.value!!.teamId!!)
                else
                    _currentUserOwnedTeams.value = repository.getOwnerTeams(repository.getCurrentUser().userId)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }
    
    fun deleteTeam() {
        viewModelScope.launch {
            try {
                repository.deleteTeam(team.value!!.teamId!!)
                joinAll()
                _team.value = null
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun sendRequestToJoinTeam() {
        viewModelScope.launch {
            try {
                repository.sendRequestToJoinTeam(team.value!!.teamId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_team_detail_request_sent_successful_message
                setCurrentUserStatus(repository.getActualRequestStatus(team.value!!.teamId!!, repository.getCurrentUser().userId))
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun cancelRequest() {
        viewModelScope.launch {
            try {
                repository.cancelUserRequest(team.value!!.teamId!!, repository.getCurrentUser().userId)
                joinAll()
                _snackbarMessage.value = R.string.fragment_team_detail_request_cancel_successful_message
                setCurrentUserStatus(repository.getActualRequestStatus(team.value!!.teamId!!, repository.getCurrentUser().userId))
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun leaveTeam() {
        viewModelScope.launch {
            try {
                repository.removeUserFromTeam(team.value!!.teamId!!, repository.getCurrentUser().userId)
                joinAll()
                setCurrentUserStatus(repository.getActualRequestStatus(team.value!!.teamId!!, repository.getCurrentUser().userId))
                _players.value = repository.getPlayersByTeam(team.value!!.teamId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_team_detail_team_left_message
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun removeUser(userId: Int) {
        viewModelScope.launch {
            try {
                repository.removeUserFromTeam(team.value!!.teamId!!, userId)
                joinAll()
                _snackbarMessage.value = R.string.fragment_team_detail_user_removed_message
                _players.value = repository.getPlayersByTeam(team.value!!.teamId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun acceptPlayerRequest(userId: Int) {
        viewModelScope.launch {
            try {
                repository.acceptUserRequest(team.value!!.teamId!!, userId)
                joinAll()
                _snackbarMessage.value = R.string.fragment_team_detail_request_accept_successful_message
                _players.value = repository.getPlayersByTeam(team.value!!.teamId!!)
                _requestsUsers.value = repository.getPendingRequestsUsersForTeam(team.value!!.teamId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun rejectPlayerRequest(userId: Int) {
        viewModelScope.launch {
            try {
                repository.rejectUserRequest(team.value!!.teamId!!, userId)
                joinAll()
                _snackbarMessage.value = R.string.fragment_team_detail_request_reject_successful_message
                _players.value = repository.getPlayersByTeam(team.value!!.teamId!!)
                _requestsUsers.value = repository.getPendingRequestsUsersForTeam(team.value!!.teamId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun isCurrentUserOwner(): Boolean = team.value!!.ownerId == repository.getCurrentUser().userId

    fun isCurrentUserAdmin(): Boolean = repository.getCurrentUser().roles.contains(Role.ADMINISTRATOR)

    private fun setCurrentUserStatus(requestStatusId: Int?) {
        if (requestStatusId != null)
            _currentUserStatus.value = RequestStatus.getByOrdinal(requestStatusId - 1)
        else {
            _currentUserStatus.value = null
        }
    }
}