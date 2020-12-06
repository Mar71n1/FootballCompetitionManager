package pl.pwr.footballcompetitionmanager.fragments.teamupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

class TeamUpdateViewModelFactory(private val repository: IRepository, private val teamId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamUpdateViewModel::class.java))
            return TeamUpdateViewModel(repository, teamId) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}