package pl.pwr.footballcompetitionmanager.model

enum class CompetitionStatus {
    PLANNING,
    IN_PROGRESS,
    COMPLETED;

    companion object {
        private val VALUES = values()
        fun getByOrdinal(ordinal: Int) = VALUES[ordinal]
    }
}