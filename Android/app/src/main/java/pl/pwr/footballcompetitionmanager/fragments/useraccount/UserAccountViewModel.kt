package pl.pwr.footballcompetitionmanager.fragments.useraccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.pwr.footballcompetitionmanager.model.User
import pl.pwr.footballcompetitionmanager.repository.IRepository

class UserAccountViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        _user.value = repository.getCurrentUser()
    }
}