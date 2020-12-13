package pl.pwr.footballcompetitionmanager.fragments.leaguedetail

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

class LeagueDetailViewModel(
    private val repository: IRepository,
    competitionId: Int
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _leagueSeason = MutableLiveData<LeagueSeason>()
    val leagueSeason: LiveData<LeagueSeason>
        get() = _leagueSeason

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>>
        get() = _matches

    private val _results = MutableLiveData<List<Match>>()
    val results: LiveData<List<Match>>
        get() = _results

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() = _teams

    private val _requests = MutableLiveData<List<Team>>()
    val requests: LiveData<List<Team>>
        get() = _requests

    private val _ownedTeams = MutableLiveData<List<Team>>()
    val ownedTeams: LiveData<List<Team>>
        get() = _ownedTeams

    private val _table = MutableLiveData<List<LeagueTableRow>>()
    val table: LiveData<List<LeagueTableRow>>
        get() = _table

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage
//    val snackbarMessage: LiveData<Int>
//        get() = _snackbarMessage

    init {
        Timber.d("init")
        viewModelScope.launch {
            try {
                _leagueSeason.value = repository.getLeagueSeasonByCompetitionId(competitionId)
                joinAll()
                _matches.value = repository.getCompetitionMatches(leagueSeason.value!!.competition.competitionId!!)
                _results.value = repository.getCompetitionResults(leagueSeason.value!!.competition.competitionId!!)
                _teams.value = repository.getCompetitionTeams(leagueSeason.value!!.competition.competitionId!!)
                _ownedTeams.value = repository.getOwnerTeams(getCurrentUserId())
                _requests.value = repository.getPendingRequestsTeamsForCompetition(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _table.value = prepareTable()
                _loading.value = false
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                if (!loading.value!!) {
                    _leagueSeason.value = repository.getLeagueSeasonByCompetitionId(leagueSeason.value!!.competition.competitionId!!)
                    joinAll()
                    _matches.value = repository.getCompetitionMatches(leagueSeason.value!!.competition.competitionId!!)
                    _results.value = repository.getCompetitionResults(leagueSeason.value!!.competition.competitionId!!)
                    _teams.value = repository.getCompetitionTeams(leagueSeason.value!!.competition.competitionId!!)
                    _ownedTeams.value = repository.getOwnerTeams(getCurrentUserId())
                    _requests.value = repository.getPendingRequestsTeamsForCompetition(leagueSeason.value!!.competition.competitionId!!)
                    joinAll()
                    _table.value = prepareTable()
                }
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun deleteLeague() {
        viewModelScope.launch {
            try {
                repository.deleteLeagueSeason(leagueSeason.value!!.leagueSeasonId!!)
                joinAll()
                _leagueSeason.value = null
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun startCompetition() {
        viewModelScope.launch {
            try {
                repository.startCompetition(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _leagueSeason.value = repository.getLeagueSeasonByCompetitionId(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_competition_started_snackbar_message
                _loading.value = false
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun finishCompetition() {
        viewModelScope.launch {
            try {
                repository.finishCompetition(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _leagueSeason.value = repository.getLeagueSeasonByCompetitionId(leagueSeason.value!!.competition.competitionId!!)
                _matches.value = repository.getCompetitionMatches(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_competition_finished_snackbar_message
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun sendRequestToJoin(position: Int) {
        viewModelScope.launch {
            try {
                repository.sendRequestToJoinCompetition(leagueSeason.value!!.competition.competitionId!!, ownedTeams.value!![position].teamId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_request_sent_successful_message
                _requests.value = repository.getPendingRequestsTeamsForCompetition(leagueSeason.value!!.competition.competitionId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun cancelRequest() {
        viewModelScope.launch {
            try {
                repository.cancelTeamRequest(leagueSeason.value!!.competition.competitionId!!, requests.value!!.first{ it.ownerId == getCurrentUserId() }.teamId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_request_cancel_successful_message
                _requests.value = repository.getPendingRequestsTeamsForCompetition(leagueSeason.value!!.competition.competitionId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun leaveCompetition() {
        viewModelScope.launch {
            try {
                repository.removeTeamFromCompetition(leagueSeason.value!!.competition.competitionId!!, teams.value!!.first { it.ownerId == getCurrentUserId() }.teamId!!)
                joinAll()
                _teams.value = repository.getCompetitionTeams(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_competition_left_message
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun removeTeam(teamId: Int) {
        viewModelScope.launch {
            try {
                repository.removeTeamFromCompetition(leagueSeason.value!!.competition.competitionId!!, teamId)
                joinAll()
                _teams.value = repository.getCompetitionTeams(leagueSeason.value!!.competition.competitionId!!)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_team_removed_message
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun acceptTeamRequest(teamId: Int) {
        viewModelScope.launch {
            try {
                repository.acceptTeamRequest(leagueSeason.value!!.competition.competitionId!!, teamId)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_request_accept_successful_message
                _teams.value = repository.getCompetitionTeams(leagueSeason.value!!.competition.competitionId!!)
                _requests.value = repository.getPendingRequestsTeamsForCompetition(leagueSeason.value!!.competition.competitionId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun rejectTeamRequest(teamId: Int) {
        viewModelScope.launch {
            try {
                repository.rejectTeamRequest(leagueSeason.value!!.competition.competitionId!!, teamId)
                joinAll()
                _snackbarMessage.value = R.string.fragment_league_detail_request_reject_successful_message
                _requests.value = repository.getPendingRequestsTeamsForCompetition(leagueSeason.value!!.competition.competitionId!!)
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun getCurrentUserId(): Int = repository.getCurrentUser().userId

    fun isCurrentUserOwner(): Boolean = leagueSeason.value!!.competition.ownerId == repository.getCurrentUser().userId

    fun isCurrentUserAdmin(): Boolean = repository.getCurrentUser().roles.contains(Role.ADMINISTRATOR)

    fun getPossibleHomeTeams(): List<Team> {
        return if (leagueSeason.value!!.doubleMatches) {
            teams.value!!.filter { getNumberOfHomeTeamMatches(it.teamId!!) < (leagueSeason.value!!.competition.numberOfTeams - 1) }
        } else {
            teams.value!!.filter { getNumberOfTeamMatches(it.teamId!!) < (leagueSeason.value!!.competition.numberOfTeams - 1) }
        }
    }

    fun getPossibleAwayTeam(teamId: Int): List<Team> {
        return if (leagueSeason.value!!.doubleMatches) {
            val opponentAwayTeamIds = getAllMatches().filter { it.homeTeamId == teamId }.map { it.awayTeamId }
            teams.value!!.filter { !opponentAwayTeamIds.contains(it.teamId) }.filter { it.teamId != teamId }
        } else {
            val opponentHomeTeamIds = getAllMatches().filter { it.homeTeamId == teamId }.map { it.awayTeamId }
            val opponentAwayTeamIds = getAllMatches().filter { it.awayTeamId == teamId }.map { it.homeTeamId }
            val allOpponentTeamIds = opponentHomeTeamIds.union(opponentAwayTeamIds)
            teams.value!!.filter { !allOpponentTeamIds.contains(it.teamId) }.filter { it.teamId != teamId }
        }
    }

    private fun getAllMatches(): List<Match> = matches.value!!.union(results.value!!).toList()

    private fun getNumberOfHomeTeamMatches(teamId: Int): Int = getAllMatches().filter { it.homeTeamId == teamId }.size

    private fun getNumberOfTeamMatches(teamId: Int): Int {
        val numberOfPlannedMatches = matches.value!!.filter { it.homeTeamId == teamId || it.awayTeamId == teamId }.size
        val numberOfPlayedMatches = results.value!!.filter { it.homeTeamId == teamId || it.awayTeamId == teamId }.size
        return numberOfPlannedMatches + numberOfPlayedMatches
    }

    private fun prepareTable(): List<LeagueTableRow> {
        val tableRowsData = mutableListOf<LeagueTableRow>()
        val namesOfTeamsWithoutResult = teams.value!!.map { it.name }.toMutableList()

        results.value!!.forEach {
            with (it) {
                namesOfTeamsWithoutResult.removeIf { teamName -> teamName == homeTeamName || teamName == awayTeamName }

                // Drużyna domowa
                val homeTeamTableRow = tableRowsData.find { it.teamName == homeTeamName }
                if (homeTeamTableRow == null) {
                    tableRowsData.add(
                        pl.pwr.footballcompetitionmanager.model.LeagueTableRow(
                            homeTeamName!!,
                            1,
                            if (homeTeamGoals!! > awayTeamGoals!!) 1 else 0,
                            if (homeTeamGoals!! == awayTeamGoals!!) 1 else 0,
                            if (homeTeamGoals!! < awayTeamGoals!!) 1 else 0,
                            homeTeamGoals!!,
                            awayTeamGoals!!
                        )
                    )
                } else {
                    kotlin.with(homeTeamTableRow) {
                        playedMatches++
                        if (homeTeamGoals!! > awayTeamGoals!!) wins++
                        if (homeTeamGoals!! == awayTeamGoals!!) draws++
                        if (homeTeamGoals!! < awayTeamGoals!!) loses++
                        goalsFor += homeTeamGoals!!
                        goalsAgainst += awayTeamGoals!!
                    }
                }

                // Drużyna wyjazdowa
                val awayTeamTableRow = tableRowsData.find { it.teamName == awayTeamName }
                if (awayTeamTableRow == null) {
                    tableRowsData.add(
                        pl.pwr.footballcompetitionmanager.model.LeagueTableRow(
                            awayTeamName!!,
                            1,
                            if (homeTeamGoals!! < awayTeamGoals!!) 1 else 0,
                            if (homeTeamGoals!! == awayTeamGoals!!) 1 else 0,
                            if (homeTeamGoals!! > awayTeamGoals!!) 1 else 0,
                            awayTeamGoals!!,
                            homeTeamGoals!!
                        )
                    )
                } else {
                    kotlin.with(awayTeamTableRow) {
                        playedMatches++
                        if (homeTeamGoals!! < awayTeamGoals!!) wins++
                        if (homeTeamGoals!! == awayTeamGoals!!) draws++
                        if (homeTeamGoals!! > awayTeamGoals!!) loses++
                        goalsFor += awayTeamGoals!!
                        goalsAgainst += homeTeamGoals!!
                    }
                }
            }
        }

        namesOfTeamsWithoutResult.forEach {
            tableRowsData.add(LeagueTableRow(
                it,
                0,
                0,
                0,
                0,
                0,
                0
            ))
        }

        tableRowsData.sortByDescending { it.playedMatches }
        tableRowsData.sortByDescending { it.goalsFor }
        tableRowsData.sortByDescending { it.goalsFor - it.goalsAgainst }
        tableRowsData.sortByDescending { it.wins }
        tableRowsData.sortByDescending { it.points }

        val numberOfTeams = leagueSeason.value!!.competition.numberOfTeams
        while (tableRowsData.size < numberOfTeams) {
            tableRowsData.add(LeagueTableRow(
                "Unknown",
                0,
                0,
                0,
                0,
                0,
                0
            ))
        }

        return tableRowsData
    }
}