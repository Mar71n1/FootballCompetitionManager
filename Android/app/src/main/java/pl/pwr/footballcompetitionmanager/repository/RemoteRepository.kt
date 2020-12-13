package pl.pwr.footballcompetitionmanager.repository

import kotlinx.coroutines.joinAll
import okhttp3.Credentials
import pl.pwr.footballcompetitionmanager.model.*
import pl.pwr.footballcompetitionmanager.network.Api
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketTimeoutException
import kotlin.Exception

class RemoteRepository : IRepository {

    companion object {
        private var user: User? = null

        private fun createCredentialsBasicAuthentication(): String {
            return Credentials.basic(user!!.username, user!!.password)
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Competitions
    override suspend fun getCompetition(competitionId: Int): Competition {
        try {
            return Api.retrofitService.getCompetition(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getCompetitions(): List<Competition> {
        try {
            return Api.retrofitService.getCompetitions(createCredentialsBasicAuthentication())
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getCompetitionsForUser(userId: Int): List<Competition> {
        try {
            return Api.retrofitService.getCompetitionsForUser(createCredentialsBasicAuthentication(), userId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getCompetitionsByTeam(teamId: Int): List<Competition> {
        try {
            return Api.retrofitService.getCompetitionsByTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun searchCompetitionsByName(name: String): List<Competition> {
        try {
            return Api.retrofitService.searchCompetitionsByName(createCredentialsBasicAuthentication(), name)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun startCompetition(competitionId: Int) {
        try {
            Api.retrofitService.startCompetition(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun finishCompetition(competitionId: Int) {
        try {
            Api.retrofitService.finishCompetition(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CompetitionTeams
    override suspend fun sendRequestToJoinCompetition(competitionId: Int, teamId: Int) {
        try {
            Api.retrofitService.sendRequestToJoinCompetition(createCredentialsBasicAuthentication(), competitionId, teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun cancelTeamRequest(competitionId: Int, teamId: Int) {
        try {
            Api.retrofitService.cancelTeamRequest(createCredentialsBasicAuthentication(), competitionId, teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun removeTeamFromCompetition(competitionId: Int, teamId: Int) {
        try {
            Api.retrofitService.removeTeamFromCompetition(createCredentialsBasicAuthentication(), competitionId, teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun acceptTeamRequest(competitionId: Int, teamId: Int) {
        try {
            Api.retrofitService.acceptTeamRequest(createCredentialsBasicAuthentication(), competitionId, teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun rejectTeamRequest(competitionId: Int, teamId: Int) {
        try {
            Api.retrofitService.rejectTeamRequest(createCredentialsBasicAuthentication(), competitionId, teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // LeagueSeasons
    override suspend fun getLeagueSeason(leagueSeasonId: Int): LeagueSeason {
        try {
            return Api.retrofitService.getLeagueSeason(createCredentialsBasicAuthentication(), leagueSeasonId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getLeagueSeasonByCompetitionId(competitionId: Int): LeagueSeason {
        try {
            return Api.retrofitService.getLeagueSeasonByCompetitionId(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun createLeagueSeason(leagueSeason: LeagueSeason): LeagueSeason {
        try {
            return Api.retrofitService.createLeagueSeason(createCredentialsBasicAuthentication(), leagueSeason)
        } catch (httpException: HttpException) {
            if (httpException.message!!.contains("400"))
                throw IllegalArgumentException()
            else
                throw httpException
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun updateLeagueSeason(leagueSeason: LeagueSeason): LeagueSeason {
        try {
            return Api.retrofitService.updateLeagueSeason(createCredentialsBasicAuthentication(), leagueSeason.leagueSeasonId!!, leagueSeason)
        } catch (httpException: HttpException) {
            if (httpException.message!!.contains("400"))
                throw IllegalArgumentException()
            else
                throw httpException
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun deleteLeagueSeason(leagueSeasonId: Int) {
        try {
            Api.retrofitService.deleteLeagueSeason(createCredentialsBasicAuthentication(), leagueSeasonId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Matches
    override suspend fun getMatch(matchId: Int): Match? {
        try {
            return Api.retrofitService.getMatch(createCredentialsBasicAuthentication(), matchId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getMatches(): List<Match> {
        try {
            return Api.retrofitService.getMatches(createCredentialsBasicAuthentication())
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getMatchesForUser(userId: Int): List<Match> {
        try {
            return Api.retrofitService.getMatchesForUser(createCredentialsBasicAuthentication(), userId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getIncomingMatchesByTeam(teamId: Int): List<Match> {
        try {
            return Api.retrofitService.getIncomingMatchesByTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getLatestResultsByTeam(teamId: Int): List<Match> {
        try {
            return Api.retrofitService.getLatestResultsByTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getCompetitionMatches(competitionId: Int): List<Match> {
        try {
            return Api.retrofitService.getCompetitionMatches(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getCompetitionResults(competitionId: Int): List<Match> {
        try {
            return Api.retrofitService.getCompetitionResults(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun createMatch(match: Match): Match {
        try {
            return Api.retrofitService.createMatch(createCredentialsBasicAuthentication(), match)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun deleteMatch(matchId: Int) {
        try {
            Api.retrofitService.deleteMatch(createCredentialsBasicAuthentication(), matchId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun updateMatch(match: Match): Match {
        try {
            return Api.retrofitService.updateMatch(createCredentialsBasicAuthentication(), match.matchId!!, match)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun setMatchScore(matchId: Int, homeTeamGoals: Int, awayTeamGoals: Int): Match {
        try {
            return Api.retrofitService.setMatchScore(createCredentialsBasicAuthentication(), matchId, homeTeamGoals, awayTeamGoals)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun acceptMatchScore(matchId: Int): Match {
        try {
            return Api.retrofitService.acceptMatchScore(createCredentialsBasicAuthentication(), matchId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun rejectMatchScore(matchId: Int): Match {
        try {
            return Api.retrofitService.rejectMatchScore(createCredentialsBasicAuthentication(), matchId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun acceptMatchProposal(matchId: Int): Match {
        try {
            return Api.retrofitService.acceptMatchProposal(createCredentialsBasicAuthentication(), matchId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun rejectMatchProposal(matchId: Int) {
        try {
            Api.retrofitService.rejectMatchProposal(createCredentialsBasicAuthentication(), matchId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Reports
    override suspend fun getUnsolvedReports(): List<Report> {
        try {
            return Api.retrofitService.getReports(createCredentialsBasicAuthentication(), false)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getSolvedReports(): List<Report> {
        try {
            return Api.retrofitService.getReports(createCredentialsBasicAuthentication(), true)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun createReport(report: Report): Report {
        if (255 < report.description.length) {
            Timber.d("Opis błędu: ${report.description}, długość: ${report.description.length}")
            throw IllegalArgumentException("Za długi opis błędu")
        } else {
            try {
                return Api.retrofitService.createReport(createCredentialsBasicAuthentication(), report)
            } catch (exception: Exception) {
                Timber.e(exception)
                throw exception
            }
        }
    }

    override suspend fun markReportAsSolved(reportId: Int): Boolean {
        try {
            Api.retrofitService.markReportAsSolved(createCredentialsBasicAuthentication(), reportId)
            joinAll()
            return true
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Teams
    override suspend fun getTeam(teamId: Int): Team? {
        return try {
            Api.retrofitService.getTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: HttpException) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getTeams(): List<Team> {
        try {
            return Api.retrofitService.getTeams(createCredentialsBasicAuthentication())
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun updateTeam(team: Team): Team {
        try {
            return Api.retrofitService.updateTeam(createCredentialsBasicAuthentication(), team.teamId!!, team)
        } catch (httpException: Exception) {
            if (httpException.message!!.contains("400"))
                throw IllegalArgumentException()
            else
                throw Exception("Unknown HttpException")
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun deleteTeam(teamId: Int) {
        try {
            Api.retrofitService.deleteTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getTeamsForUser(userId: Int): List<Team> {
        return try {
            Api.retrofitService.getTeamsForUser(createCredentialsBasicAuthentication(), userId)
        } catch (httpException: HttpException) {
            if (httpException.message!!.contains("404"))
                listOf()
            else
                throw Exception("Unknown HttpException")
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getOwnerTeams(ownerId: Int): List<Team> {
        try {
            return Api.retrofitService.getOwnerTeams(createCredentialsBasicAuthentication(), ownerId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getCompetitionTeams(competitionId: Int): List<Team> {
        try {
            return Api.retrofitService.getCompetitionTeams(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun searchTeamsByName(name: String): List<Team> {
        return Api.retrofitService.searchTeamsByName(createCredentialsBasicAuthentication(), name)
    }

    override suspend fun getPendingRequestsTeamsForCompetition(competitionId: Int): List<Team> {
        try {
            return Api.retrofitService.getPendingRequestsTeamsForCompetition(createCredentialsBasicAuthentication(), competitionId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun createTeam(team: Team): Team {
        try {
            return Api.retrofitService.createTeam(createCredentialsBasicAuthentication(), team)
        } catch (httpException: Exception) {
            if (httpException.message!!.contains("400"))
                throw IllegalArgumentException()
            else
                throw Exception("Unknown HttpException")
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun sendRequestToJoinTeam(teamId: Int): Boolean {
        try {
            return Api.retrofitService.sendRequestToJoinTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (httpException: Exception) {
            if (httpException.message!!.contains("400"))
                throw IllegalArgumentException()
            else
                throw Exception("Unknown HttpException")
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // TeamUsers
    override suspend fun getActualRequestStatus(teamId: Int, userId: Int): Int? {
        return try {
            Api.retrofitService.getActualRequestStatus(createCredentialsBasicAuthentication(), teamId, userId)
        } catch (httpException: Exception) {
            if (httpException.message!!.contains("404"))
                null
            else
                throw Exception("Unknown HttpException")
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun cancelUserRequest(teamId: Int, userId: Int) {
        try {
            Api.retrofitService.cancelUserRequest(createCredentialsBasicAuthentication(), teamId, userId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun removeUserFromTeam(teamId: Int, userId: Int) {
        try {
            Api.retrofitService.removeUserFromTeam(createCredentialsBasicAuthentication(), teamId, userId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun acceptUserRequest(teamId: Int, userId: Int) {
        try {
            Api.retrofitService.acceptUserRequest(createCredentialsBasicAuthentication(), teamId, userId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun rejectUserRequest(teamId: Int, userId: Int) {
        try {
            Api.retrofitService.rejectUserRequest(createCredentialsBasicAuthentication(), teamId, userId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Users
    override suspend fun register(email: String, username: String, password: String, confirmPassword: String) {
        return try {
            Api.retrofitService.register(email, username, password, confirmPassword)         //TeamsApi.retrofitService.register(UserToRegister(email, username, password, confirmPassword))
            joinAll()
        } catch (httpException: Exception) {
            if (httpException.message!!.contains("400"))
                throw IllegalArgumentException()
            else
                throw Exception("Unknown HttpException")
        } catch (exception: Exception) {
            Timber.d("Register failed")
            throw exception
        }
    }

    override suspend fun login(username: String, password: String): Boolean {
        return try {
            val user = Api.retrofitService.login(Credentials.basic(username, password))
            joinAll()
            Companion.user = user
            true
        } catch (exception: HttpException) {
            Timber.d("Login failed")
            throw exception
        } catch (exception: SocketTimeoutException) {
            Timber.d("Timeout socket exception")
            throw exception
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override fun logout() {
        user = null
    }

    override suspend fun changeData(user: User): User {
        try {
            val user = Api.retrofitService.changeData(createCredentialsBasicAuthentication(), user.userId, user)
            joinAll()
            Companion.user = user
            return user
        } catch (exception: Exception) {
            Timber.d("Unknown exception")
            throw exception
        }
    }

    override fun getCurrentUser(): User {
        return user!!
    }

    override suspend fun getPlayersByTeam(teamId: Int): List<User> {
        try {
            return Api.retrofitService.getUsersByTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }

    override suspend fun getPendingRequestsUsersForTeam(teamId: Int): List<User> {
        try {
            return Api.retrofitService.getPendingRequestsUsersForTeam(createCredentialsBasicAuthentication(), teamId)
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}