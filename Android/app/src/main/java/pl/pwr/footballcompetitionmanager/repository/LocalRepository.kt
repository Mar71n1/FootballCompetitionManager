package pl.pwr.footballcompetitionmanager.repository

import pl.pwr.footballcompetitionmanager.model.*

class LocalRepository : IRepository {

    private val teams: List<Team> = listOf(
//        Team(1, "Barcelona"),
//        Team(2, "Madrid")
    )

    override suspend fun getCompetition(competitionId: Int): Competition {
        TODO("Not yet implemented")
    }

    // Competitions
    override suspend fun getCompetitions(): List<Competition> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionsForUser(userId: Int): List<Competition> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionsByTeam(teamId: Int): List<Competition> {
        TODO("Not yet implemented")
    }

    override suspend fun searchCompetitionsByName(name: String): List<Competition> {
        TODO("Not yet implemented")
    }

    override suspend fun startCompetition(competitionId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun finishCompetition(competitionId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun sendRequestToJoinCompetition(competitionId: Int, teamId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun cancelTeamRequest(competitionId: Int, teamId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTeamFromCompetition(competitionId: Int, teamId: Int) {
        TODO("Not yet implemented")
    }

    // LeagueSeasons
    override suspend fun createLeagueSeason(leagueSeason: LeagueSeason): LeagueSeason {
        TODO("Not yet implemented")
    }

    override suspend fun updateLeagueSeason(leagueSeason: LeagueSeason): LeagueSeason {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLeagueSeason(leagueSeasonId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun register(email: String, username: String, password: String, confirmPassword: String): Boolean {
        TODO("Not yet implemented")
    }

    // Users
    override suspend fun login(username: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun changeData(user: User): User {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): User {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayersByTeam(teamId: Int): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getPendingRequestsUsersForTeam(teamId: Int): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getMatch(matchId: Int): Match? {
        TODO("Not yet implemented")
    }

    override suspend fun getMatches(): List<Match> {
        TODO("Not yet implemented")
    }

    override suspend fun getMatchesForUser(userId: Int): List<Match> {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomingMatchesByTeam(teamId: Int): List<Match> {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestResultsByTeam(teamId: Int): List<Match> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionMatches(competitionId: Int): List<Match> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionResults(competitionId: Int): List<Match> {
        TODO("Not yet implemented")
    }

    override suspend fun createMatch(match: Match): Match {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMatch(matchId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMatch(match: Match): Match {
        TODO("Not yet implemented")
    }

    override suspend fun setMatchScore(
        matchId: Int,
        homeTeamGoals: Int,
        awayTeamGoals: Int
    ): Match {
        TODO("Not yet implemented")
    }

    override suspend fun acceptMatchScore(matchId: Int): Match {
        TODO("Not yet implemented")
    }

    override suspend fun rejectMatchScore(matchId: Int): Match {
        TODO("Not yet implemented")
    }

    override suspend fun acceptMatchProposal(matchId: Int): Match {
        TODO("Not yet implemented")
    }

    override suspend fun rejectMatchProposal(matchId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTeam(teamId: Int): Team? {
        return teams.find { team ->
            team.teamId == teamId
        }
    }

    override suspend fun getTeams(): List<Team> {
        return teams
    }

    override suspend fun updateTeam(team: Team): Team {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTeam(teamId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTeamsForUser(userId: Int): List<Team> {
        TODO("Not yet implemented")
    }

    override suspend fun getOwnerTeams(ownerId: Int): List<Team> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompetitionTeams(competitionId: Int): List<Team> {
        TODO("Not yet implemented")
    }

    override suspend fun searchTeamsByName(name: String): List<Team> {
        TODO("Not yet implemented")
    }

    override suspend fun getPendingRequestsTeamsForCompetition(competitionId: Int): List<Team> {
        TODO("Not yet implemented")
    }

    override suspend fun createTeam(team: Team): Team {
        TODO("Not yet implemented")
    }

    override suspend fun sendRequestToJoinTeam(teamId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getActualRequestStatus(teamId: Int, userId: Int): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun cancelUserRequest(teamId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserFromTeam(teamId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun acceptUserRequest(teamId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun rejectUserRequest(teamId: Int, userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun acceptTeamRequest(competitionId: Int, teamId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun rejectTeamRequest(competitionId: Int, teamId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getLeagueSeason(leagueSeasonId: Int): LeagueSeason {
        TODO("Not yet implemented")
    }

    override suspend fun getLeagueSeasonByCompetitionId(competitionId: Int): LeagueSeason {
        TODO("Not yet implemented")
    }

    override suspend fun createReport(report: Report): Report {
        TODO("Not yet implemented")
    }

    override suspend fun getUnsolvedReports(): List<Report> {
        TODO("Not yet implemented")
    }

    override suspend fun getSolvedReports(): List<Report> {
        TODO("Not yet implemented")
    }

    override suspend fun markReportAsSolved(reportId: Int): Boolean {
        TODO("Not yet implemented")
    }
}