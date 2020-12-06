package pl.pwr.footballcompetitionmanager.model

class LeagueTableRow (val teamName: String,
                      var playedMatches: Int,
                      var wins: Int,
                      var draws: Int,
                      var loses: Int,
                      var goalsFor: Int,
                      var goalsAgainst: Int) {
    val points
        get() = wins * 3 + draws
}