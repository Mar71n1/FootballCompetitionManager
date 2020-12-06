package pl.pwr.footballcompetitionmanager.fragments.matchdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class MatchDetailViewModelFactory(private val repository: IRepository, private val matchId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchDetailViewModel::class.java)) {
            return MatchDetailViewModel(repository, matchId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}