package pl.pwr.footballcompetitionmanager.model

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime

class Match(@SerializedName("MatchId") val matchId: Int?,
            @SerializedName("CompetitionId") val competitionId: Int?,
            @SerializedName("CompetitionName") val competitionName: String?,
            @SerializedName("HomeTeamId") val homeTeamId: Int,
            @SerializedName("HomeTeamName") val homeTeamName: String?,
            @SerializedName("AwayTeamId") val awayTeamId: Int,
            @SerializedName("AwayTeamName") val awayTeamName: String?,
            @SerializedName("Time") private var timeString: String,
            @SerializedName("Latitude") var latitude: Double?,
            @SerializedName("Longitude") var longitude: Double?,
            @SerializedName("Length") var length: Int,
            @SerializedName("PlayersPerTeam") var playersPerTeam: Int,
            @SerializedName("HomeTeamGoals") var homeTeamGoals: Int? = null,
            @SerializedName("AwayTeamGoals") var awayTeamGoals: Int? = null,
            @SerializedName("MatchStatusId") private var matchStatusId: Int?) {

    var time: LocalDateTime
        get() = LocalDateTime.parse(timeString)
        set(value) { timeString = value.toString() }

    val matchStatus: MatchStatus?
        get() = MatchStatus.getByOrdinal(matchStatusId!! - 1)

    constructor(competitionId: Int?, homeTeamId: Int, awayTeamId: Int, time: LocalDateTime, latitude: Double?, longitude: Double?, length: Int, playersPerTeam: Int)
            : this(null, competitionId, null, homeTeamId, null, awayTeamId, null, time.toString(), latitude, longitude, length, playersPerTeam, null, null, null)
}