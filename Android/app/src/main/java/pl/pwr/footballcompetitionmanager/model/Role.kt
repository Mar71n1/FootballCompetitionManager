package pl.pwr.footballcompetitionmanager.model

enum class Role {
    USER,
    ADMINISTRATOR;

    companion object {
        private val VALUES = values()
        fun getByOrdinal(ordinal: Int) = VALUES[ordinal]
    }
}