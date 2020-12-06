package pl.pwr.footballcompetitionmanager.fragments.leaguecreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class LeagueCreateViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeagueCreateViewModel::class.java)) {
            return LeagueCreateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}