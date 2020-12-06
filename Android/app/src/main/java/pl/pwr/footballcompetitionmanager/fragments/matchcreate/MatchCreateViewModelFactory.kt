package pl.pwr.footballcompetitionmanager.fragments.matchcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class MatchCreateViewModelFactory(private val repository: IRepository, private val homeTeamId: Int, private val awayTeamId: Int, private val competitionId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchCreateViewModel::class.java)) {
            return MatchCreateViewModel(repository, homeTeamId, awayTeamId, competitionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}