package pl.pwr.footballcompetitionmanager.model

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime

class Team(@SerializedName("TeamId") val teamId: Int?,
           @SerializedName("Name") var name: String,
           @SerializedName("OwnerId") val ownerId: Int,
           @SerializedName("OwnerUsername") val ownerUsername: String?,
           @SerializedName("Description") var description: String,
           @SerializedName("CreationDate") private val creationDateString: String?) {

    val creationDate: LocalDateTime
        get() = LocalDateTime.parse(creationDateString!!.replace("Z", ""))

    constructor(name: String, ownerId: Int, description: String) : this(null, name, ownerId, null, description, null)
}