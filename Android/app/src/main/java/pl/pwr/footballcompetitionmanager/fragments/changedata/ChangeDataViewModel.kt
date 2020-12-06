package pl.pwr.footballcompetitionmanager.fragments.changedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.User
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.EmailValidator
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent
import timber.log.Timber

class ChangeDataViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _emailFieldError = MutableLiveData<Int>(null)
    val emailFieldError: LiveData<Int>
        get() = _emailFieldError

    private val _passwordFieldError = MutableLiveData<String>(null)
    val passwordFieldError: LiveData<String>
        get() = _passwordFieldError

    private val _confirmPasswordFieldError = MutableLiveData<String>(null)
    val confirmPasswordFieldError: LiveData<String>
        get() = _confirmPasswordFieldError

    private val _currentPasswordFieldError = MutableLiveData<String>(null)
    val currentPasswordFieldError: LiveData<String>
        get() = _currentPasswordFieldError

    private val _dataChangeSuccessful = SingleLiveEvent<Boolean>()
    fun getDataChangeSuccessful(): SingleLiveEvent<Boolean> = _dataChangeSuccessful

    fun changeData(email: String, newPassword: String, newPasswordRepeat: String, currentPassword: String) {
        if (!EmailValidator.isValidEmail(email)
            || (newPassword != "" && (newPassword != newPasswordRepeat || newPassword.length < Constants.MIN_PASSWORD_LENGTH || Constants.MAX_PASSWORD_LENGTH < newPassword.length))
            || currentPassword == "" || currentPassword != repository.getCurrentUser().password) {

            if (currentPassword == "")
                _currentPasswordFieldError.value = "Potwierdź aktualne hasło"
            else if (currentPassword != repository.getCurrentUser().password)
                _currentPasswordFieldError.value = "Nieprawidłowe hasło"

            if (!EmailValidator.isValidEmail(email))
                _emailFieldError.value = R.string.fragment_change_data_email_not_valid

            if (newPassword != "") {
                if (newPassword.length < Constants.MIN_PASSWORD_LENGTH || Constants.MAX_PASSWORD_LENGTH < newPassword.length) {
                    _passwordFieldError.value = "Hasło musi zawierać od 8 do 16 znaków"
                } else if (newPassword != newPasswordRepeat) {
                    _passwordFieldError.value = "Hasła są różne"
                    _confirmPasswordFieldError.value = "Hasła są różne"
                }
            }

        } else {
            viewModelScope.launch {
                val currentUser = repository.getCurrentUser()
                val updatedUser = User(currentUser.userId, email, currentUser.username, currentUser.password, currentUser.creationDateString, currentUser.rolesIds)
                if (newPassword != "")
                    updatedUser.password = newPassword

                try {
                    repository.changeData(updatedUser)
                    joinAll()
                    _dataChangeSuccessful.value = true
                } catch (exception: Exception) {
                    _emailFieldError.value = R.string.fragment_change_data_email_not_unique
                }
            }
        }
    }

    fun getCurrentUserEmail() = repository.getCurrentUser().email
}