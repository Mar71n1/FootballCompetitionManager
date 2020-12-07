package pl.pwr.footballcompetitionmanager.fragments.leaguecreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Competition
import pl.pwr.footballcompetitionmanager.model.LeagueSeason
import pl.pwr.footballcompetitionmanager.repository.IRepository
import timber.log.Timber

class LeagueCreateViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _newCompetitionId = MutableLiveData<Int>()
    val newCompetitionId: LiveData<Int>
        get() = _newCompetitionId

    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun createNewLeague(name: String, numberOfTeams: Int, doubleMatches: Boolean, matchLength: Int, playersPerTeam: Int, description: String) {
        if (name == "") {
            _errorMessage.value = R.string.fragment_league_create_empty_name
        } else if (name.length < Constants.MIN_COMPETITION_NAME_LENGTH || Constants.MAX_COMPETITION_NAME_LENGTH < name.length) {
            _errorMessage.value = R.string.fragment_league_create_name_length_not_valid
        } else if (Constants.MAX_COMPETITION_DESCRIPTION_LENGTH < description.length) {
            _errorMessage.value = R.string.fragment_league_create_description_too_long
        } else {
            val competition = Competition(name, numberOfTeams, matchLength, playersPerTeam, repository.getCurrentUser().userId, description)
            val leagueSeason = LeagueSeason(doubleMatches, competition)

            viewModelScope.launch {
                try {
                    val competitionSeason: LeagueSeason = repository.createLeagueSeason(leagueSeason)
                    _newCompetitionId.value = competitionSeason.competition.competitionId
                } catch (exception: Exception) {
                    _errorMessage.value = R.string.fragment_league_create_unique_name_error
                }
            }
        }
    }

    fun navigatedToCompetitionDetailView() {
        _newCompetitionId.value = null
    }
}