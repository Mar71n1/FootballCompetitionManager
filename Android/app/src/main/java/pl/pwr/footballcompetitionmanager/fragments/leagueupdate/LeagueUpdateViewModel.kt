package pl.pwr.footballcompetitionmanager.fragments.leagueupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.LeagueSeason
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent

class LeagueUpdateViewModel(
    private val repository: IRepository,
    leagueSeasonId: Int
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _leagueSeason = MutableLiveData<LeagueSeason>()
    val leagueSeason: LiveData<LeagueSeason>
        get() = _leagueSeason

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    private val _updated = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean>
        get() = _updated

    init {
        viewModelScope.launch {
            try {
                _leagueSeason.value = repository.getLeagueSeason(leagueSeasonId)
                joinAll()
                _loading.value = false
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun updateLeagueSeason(name: String, numberOfTeams: Int, doubleMatches: Boolean, length: Int, playersPerTeam: Int, description: String) {
        viewModelScope.launch {
            if (numberOfTeams < repository.getCompetitionTeams(leagueSeason.value!!.competition.competitionId!!).size)
                _snackbarMessage.value = R.string.fragment_league_update_invalid_number_of_teams_message
            else if (name == "") {
                _snackbarMessage.value = R.string.fragment_league_create_empty_name
            } else if (name.length < Constants.MIN_COMPETITION_NAME_LENGTH || Constants.MAX_COMPETITION_NAME_LENGTH < name.length) {
                _snackbarMessage.value = R.string.fragment_league_create_name_length_not_valid
            } else if (Constants.MAX_COMPETITION_DESCRIPTION_LENGTH < description.length) {
                _snackbarMessage.value = R.string.fragment_league_create_description_too_long
            } else {
                _loading.value = true
                _leagueSeason.value!!.competition.name = name
                _leagueSeason.value!!.competition.numberOfTeams = numberOfTeams
                _leagueSeason.value!!.doubleMatches = doubleMatches
                _leagueSeason.value!!.competition.matchLength = length
                _leagueSeason.value!!.competition.playersPerTeam = playersPerTeam
                _leagueSeason.value!!.competition.description = description
                try {
                    repository.updateLeagueSeason(_leagueSeason.value!!)
                    joinAll()
                    _updated.value = true
                } catch (exception: Exception) {
                    _loading.value = false
                    _snackbarMessage.value = R.string.fragment_league_create_unique_name_error
                }
            }
        }
    }
}