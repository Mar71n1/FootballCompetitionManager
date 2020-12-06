package pl.pwr.footballcompetitionmanager.model

import com.google.gson.annotations.SerializedName

class Competition(@SerializedName("CompetitionId") val competitionId: Int?,
                  @SerializedName("Name") var name: String,
                  @SerializedName("NumberOfTeams") var numberOfTeams: Int,
                  @SerializedName("MatchLength") var matchLength: Int,
                  @SerializedName("PlayersPerTeam") var playersPerTeam: Int,
                  @SerializedName("OwnerId") val ownerId: Int,
                  @SerializedName("OwnerUsername") val ownerUsername: String?,
                  @SerializedName("Description") var description: String?,
                  @SerializedName("CreationDate") val creationDateString: String?,
                  @SerializedName("CompetitionStatusId") private var competitionStatusId: Int) {

    var competitionStatus: CompetitionStatus = CompetitionStatus.getByOrdinal(competitionStatusId - 1)
        get() = CompetitionStatus.getByOrdinal(competitionStatusId - 1)
        set(value) {
            field = value
            competitionStatusId = competitionStatus.ordinal + 1
        }

    constructor(name: String, numberOfTeams: Int, matchLength: Int, playersPerTeam: Int, ownerId: Int, description: String? = null)
            : this(null, name, numberOfTeams, matchLength, playersPerTeam, ownerId, null, description, null, CompetitionStatus.PLANNING.ordinal+1)
}