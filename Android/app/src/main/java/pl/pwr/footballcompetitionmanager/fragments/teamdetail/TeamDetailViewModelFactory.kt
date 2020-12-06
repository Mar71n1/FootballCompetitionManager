package pl.pwr.footballcompetitionmanager.fragments.teamdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class TeamDetailViewModelFactory(private val repository: IRepository, private val teamId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamDetailViewModel::class.java)) {
            return TeamDetailViewModel(repository, teamId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}