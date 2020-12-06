package pl.pwr.footballcompetitionmanager.model

import com.google.gson.annotations.SerializedName

class LeagueSeason(@SerializedName("LeagueSeasonId") val leagueSeasonId: Int?,
                   @SerializedName("DoubleMatches") var doubleMatches: Boolean,
                   @SerializedName("Competition") val competition: Competition) {

    constructor(doubleMatches: Boolean, competition: Competition)
            : this(null, doubleMatches, competition)
}