package pl.pwr.footballcompetitionmanager.adapters

class MatchListener(val clickListener: (matchId: Int) -> Unit) {
    fun onClick(matchId: Int) = clickListener(matchId)
}