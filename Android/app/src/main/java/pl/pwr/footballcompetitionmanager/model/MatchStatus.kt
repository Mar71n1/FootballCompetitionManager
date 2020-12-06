package pl.pwr.footballcompetitionmanager.model

enum class MatchStatus {
    PROPOSED_HOME_TEAM_OWNER,
    PROPOSED_AWAY_TEAM_OWNER,
    PLANNED,
    SCORE_PROPOSED_HOME_TEAM_OWNER,
    SCORE_PROPOSED_AWAY_TEAM_OWNER,
    SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER,
    SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER,
    SCORE_ACCEPTED,
    SCORE_UNKNOWN;

    companion object {
        private val VALUES = values()
        fun getByOrdinal(ordinal: Int) = VALUES[ordinal]
    }
}