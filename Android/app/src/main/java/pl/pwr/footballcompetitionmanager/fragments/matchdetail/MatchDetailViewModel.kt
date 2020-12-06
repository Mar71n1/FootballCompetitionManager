package pl.pwr.footballcompetitionmanager.fragments.matchdetail

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
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class MatchDetailViewModel(
    private val repository: IRepository,
    private val matchId: Int
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _match = MutableLiveData<Match>()
    val match: LiveData<Match>
        get() = _match

    private val _competition = MutableLiveData<Competition>()
    val competition: LiveData<Competition>
        get() = _competition

    private lateinit var _homeTeam: Team
    private lateinit var _awayTeam: Team

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    private val _matchDeleted = SingleLiveEvent<Boolean>()
    fun getMatchDeleted(): SingleLiveEvent<Boolean> = _matchDeleted

    init {
        viewModelScope.launch {
            _match.value = repository.getMatch(matchId)
            joinAll()
            if (match.value!!.competitionId != null)
                _competition.value = repository.getCompetition(match.value!!.competitionId!!)
            _homeTeam = repository.getTeam(match.value!!.homeTeamId)!!
            _awayTeam = repository.getTeam(match.value!!.awayTeamId)!!
            joinAll()
            _loading.value = false
        }
    }

    fun setMatchScore(homeTeamGoals: Int, awayTeamGoals: Int) {
        viewModelScope.launch {
            _loading.value = true
            _match.value = repository.setMatchScore(match.value!!.matchId!!, homeTeamGoals, awayTeamGoals)
            joinAll()
            _loading.value = false
            _snackbarMessage.value = R.string.fragment_match_detail_score_set_message
        }
    }

    fun acceptMatchScore() {
        viewModelScope.launch {
            _loading.value = true
            _match.value = repository.acceptMatchScore(matchId)
            joinAll()
            _loading.value = false
            _snackbarMessage.value = R.string.fragment_match_detail_score_accepted_message
        }
    }

    fun rejectMatchScore() {
        viewModelScope.launch {
            _loading.value = true
            _match.value = repository.rejectMatchScore(matchId)
            joinAll()
            _loading.value = false
            _snackbarMessage.value = R.string.fragment_match_detail_score_rejected_message
        }
    }

    fun acceptMatchProposal() {
        viewModelScope.launch {
            _loading.value = true
            _match.value = repository.acceptMatchProposal(matchId)
            joinAll()
            _loading.value = false
            _snackbarMessage.value = R.string.fragment_match_detail_match_accepted_message
        }
    }

    fun rejectMatchProposal() {
        viewModelScope.launch {
            _loading.value = true
            repository.rejectMatchProposal(matchId)
            joinAll()
            _matchDeleted.value = true
        }
    }

    fun deleteMatch() {
        viewModelScope.launch {
            _loading.value = true
            repository.deleteMatch(matchId)
            joinAll()
            _matchDeleted.value = true
        }
    }

    fun getMatchInfo(): String? {
        val match = match.value!!

        return if (match.matchStatus == MatchStatus.PLANNED && match.time.plusMinutes(match.length.toLong()).isAfter(LocalDateTime.now()))
            "Mecz został zaplanowany"
        else if (match.matchStatus == MatchStatus.PLANNED && LocalDateTime.now().isAfter(match.time.plusMinutes(match.length.toLong())))
            "Mecz został rozegrany, czas na ustalenie wyniku do ${match.time.plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        else if (match.matchStatus == MatchStatus.SCORE_UNKNOWN)
            "Wynik meczu nie został podany"
        else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER)
            "Wynik meczu został zaproponowany przez właściciela drużyny domowej"
        else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER)
            "Wynik meczu został zaproponowany przez właściciela drużyny wyjazdowej"
        else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER)
            "Nowy wynik meczu został zaproponowany przez właściciela drużyny domowej"
        else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER)
            "Nowy wynik meczu został zaproponowany przez właściciela drużyny wyjazdowej"
        else if (match.matchStatus == MatchStatus.PROPOSED_HOME_TEAM_OWNER)
            "Mecz został zaproponowany przez właściciela drużyny domowej"
        else if (match.matchStatus == MatchStatus.PROPOSED_AWAY_TEAM_OWNER)
            "Mecz został zaproponowany przez właściciela drużyny wyjazdowej"
        else
            null
    }

    fun canCurrentUserSetScore(): Boolean {
        val match = match.value!!

        return if (match.competitionId != null) {
            // Mecz w rozgrywkach
            match.matchStatus == MatchStatus.PLANNED && getCurrentUserId() == competition.value!!.ownerId
        } else {
            // Mecz towarzyski
            if (match.matchStatus == MatchStatus.PLANNED && (getCurrentUserId() == _homeTeam.ownerId || getCurrentUserId() == _awayTeam.ownerId)) {
                true
            } else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER && getCurrentUserId() == _awayTeam.ownerId) {
                true
            } else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER && getCurrentUserId() == _homeTeam.ownerId) {
                true
            } else if (match.matchStatus == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER && getCurrentUserId() == _awayTeam.ownerId) {
                true
            } else match.matchStatus == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER && getCurrentUserId() == _homeTeam.ownerId
        }
    }

    fun isCurrentUserAdmin() = repository.getCurrentUser().roles.contains(Role.ADMINISTRATOR)

    fun isCurrentUserCompetitionOwner() = getCurrentUserId() == competition.value?.ownerId

    fun isCurrentUserHomeTeamOwner() = getCurrentUserId() == _homeTeam.ownerId

    fun isCurrentUserAwayTeamOwner() = getCurrentUserId() == _awayTeam.ownerId

    private fun getCurrentUserId() = repository.getCurrentUser().userId
}