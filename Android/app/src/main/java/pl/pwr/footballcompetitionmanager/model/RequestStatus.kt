package pl.pwr.footballcompetitionmanager.model

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    companion object {
        private val VALUES = values()
        fun getByOrdinal(ordinal: Int) = VALUES[ordinal]
    }
}