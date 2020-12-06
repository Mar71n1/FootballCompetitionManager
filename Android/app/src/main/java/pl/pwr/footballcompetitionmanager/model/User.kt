package pl.pwr.footballcompetitionmanager.model

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime

data class User(@SerializedName("UserId") val userId: Int,
                @SerializedName("Email") var email: String,
                @SerializedName("Username") val username: String,
                @SerializedName("Password") var password: String,
                @SerializedName("CreationDate") val creationDateString: String,
                @SerializedName("RolesIds") val rolesIds: List<Int>) {

    val creationDate: LocalDateTime
        get() = LocalDateTime.parse(creationDateString)

    val roles: List<Role>
        get() = rolesIds.map { Role.getByOrdinal(it - 1) }
}