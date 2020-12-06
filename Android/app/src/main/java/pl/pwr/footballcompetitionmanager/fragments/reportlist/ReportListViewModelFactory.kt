package pl.pwr.footballcompetitionmanager.fragments.reportlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class ReportListViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportListViewModel::class.java)) {
            return ReportListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}