package pl.pwr.footballcompetitionmanager.fragments.leagueupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class LeagueUpdateViewModelFactory(private val repository: IRepository, private val leagueSeasonId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeagueUpdateViewModel::class.java))
            return LeagueUpdateViewModel(repository, leagueSeasonId) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}