package pl.pwr.footballcompetitionmanager.fragments.teamcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class TeamCreateViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamCreateViewModel::class.java)) {
            return TeamCreateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}