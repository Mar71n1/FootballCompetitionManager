package pl.pwr.footballcompetitionmanager.fragments.matchupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class MatchUpdateViewModelFactory(private val repository: IRepository, private val matchId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchUpdateViewModel::class.java)) {
            return MatchUpdateViewModel(repository, matchId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}