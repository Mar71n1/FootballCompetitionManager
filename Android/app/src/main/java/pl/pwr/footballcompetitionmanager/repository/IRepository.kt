package pl.pwr.footballcompetitionmanager.repository

import pl.pwr.footballcompetitionmanager.model.*
import retrofit2.Response
import retrofit2.http.*

interface IRepository {

    // Competitions
    suspend fun getCompetition(competitionId: Int): Competition
    suspend fun getCompetitions(): List<Competition>
    suspend fun getCompetitionsForUser(userId: Int): List<Competition>
    suspend fun getCompetitionsByTeam(teamId: Int): List<Competition>
    suspend fun searchCompetitionsByName(name: String): List<Competition>
    suspend fun startCompetition(competitionId: Int)
    suspend fun finishCompetition(competitionId: Int)

    // CompetitionTeams
    suspend fun sendRequestToJoinCompetition(competitionId: Int, teamId: Int)
    suspend fun cancelTeamRequest(competitionId: Int, teamId: Int)
    suspend fun removeTeamFromCompetition(competitionId: Int, teamId: Int)
    suspend fun acceptTeamRequest(competitionId: Int, teamId: Int)
    suspend fun rejectTeamRequest(competitionId: Int, teamId: Int)

    // LeagueSeasons
    suspend fun getLeagueSeason(leagueSeasonId: Int): LeagueSeason
    suspend fun getLeagueSeasonByCompetitionId(competitionId: Int): LeagueSeason
    suspend fun createLeagueSeason(leagueSeason: LeagueSeason): LeagueSeason
    suspend fun updateLeagueSeason(leagueSeason: LeagueSeason): LeagueSeason
    suspend fun deleteLeagueSeason(leagueSeasonId: Int)

    // Matches
    suspend fun getMatch(matchId: Int): Match?
    suspend fun getMatches(): List<Match>
    suspend fun getMatchesForUser(userId: Int): List<Match>
    suspend fun getIncomingMatchesByTeam(teamId: Int): List<Match>
    suspend fun getLatestResultsByTeam(teamId: Int): List<Match>
    suspend fun getCompetitionMatches(competitionId: Int): List<Match>
    suspend fun getCompetitionResults(competitionId: Int): List<Match>
    suspend fun createMatch(match: Match): Match
    suspend fun deleteMatch(matchId: Int)
    suspend fun updateMatch(match: Match): Match
    suspend fun setMatchScore(matchId: Int, homeTeamGoals: Int, awayTeamGoals: Int): Match
    suspend fun acceptMatchScore(matchId: Int): Match
    suspend fun rejectMatchScore(matchId: Int): Match
    suspend fun acceptMatchProposal(matchId: Int): Match
    suspend fun rejectMatchProposal(matchId: Int)

    // Reports
    suspend fun getUnsolvedReports(): List<Report>
    suspend fun getSolvedReports(): List<Report>
    suspend fun createReport(report: Report): Report
    suspend fun markReportAsSolved(reportId: Int): Boolean

    // Teams
    suspend fun getTeam(teamId: Int): Team?
    suspend fun getTeams(): List<Team>
    suspend fun updateTeam(team: Team): Team
    suspend fun deleteTeam(teamId: Int)
    suspend fun getTeamsForUser(userId: Int): List<Team>
    suspend fun getOwnerTeams(ownerId: Int): List<Team>
    suspend fun getCompetitionTeams(competitionId: Int): List<Team>
    suspend fun searchTeamsByName(name: String): List<Team>
    suspend fun getPendingRequestsTeamsForCompetition(competitionId: Int): List<Team>
    suspend fun getPendingRequestsUsersForTeam(teamId: Int): List<User> //???????????????????????????????????????????????????????????????????
    suspend fun createTeam(team: Team): Team
    suspend fun sendRequestToJoinTeam(teamId: Int): Boolean

    // TeamUsers
    suspend fun getActualRequestStatus(teamId: Int, userId: Int): Int?
    suspend fun cancelUserRequest(teamId: Int, userId: Int)
    suspend fun removeUserFromTeam(teamId: Int, userId: Int)
    suspend fun acceptUserRequest(teamId: Int, userId: Int)
    suspend fun rejectUserRequest(teamId: Int, userId: Int)

    // User
    suspend fun register(email: String, username: String, password: String, confirmPassword: String): Boolean
    suspend fun login(username: String, password: String): Boolean
    fun logout()
    suspend fun changeData(user: User): User
    fun getCurrentUser(): User
    suspend fun getPlayersByTeam(teamId: Int): List<User>
}