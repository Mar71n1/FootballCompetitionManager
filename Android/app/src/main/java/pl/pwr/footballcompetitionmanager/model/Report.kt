package pl.pwr.footballcompetitionmanager.model

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime

class Report (@SerializedName("ReportId") val reportId: Int?,
              @SerializedName("Description") val description: String,
              @SerializedName("IsSolved") val isSolved: Boolean,
              @SerializedName("UserId") val userId: Int,
              @SerializedName("Username") val username: String?,
              @SerializedName("SentDate") private val sentDateString: String?,
              @SerializedName("SolvedDate") private val solvedDateString: String?) {

    val sentDate: LocalDateTime
        get() = LocalDateTime.parse(sentDateString)

    val solvedDate: LocalDateTime
        get() = LocalDateTime.parse(solvedDateString)

    constructor(description: String, userId: Int) : this(null, description, false, userId, null, null, null)
}