package pl.pwr.footballcompetitionmanager.fragments.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.pwr.footballcompetitionmanager.repository.IRepository

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}