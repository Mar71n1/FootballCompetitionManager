package pl.pwr.footballcompetitionmanager.fragments.reportcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class ReportCreateViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportCreateViewModel::class.java)) {
            return ReportCreateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}