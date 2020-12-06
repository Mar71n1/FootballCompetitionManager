package pl.pwr.footballcompetitionmanager.fragments.leaguedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class LeagueDetailViewModelFactory(private val repository: IRepository, private val leagueSeasonId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeagueDetailViewModel::class.java)) {
            return LeagueDetailViewModel(repository, leagueSeasonId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}