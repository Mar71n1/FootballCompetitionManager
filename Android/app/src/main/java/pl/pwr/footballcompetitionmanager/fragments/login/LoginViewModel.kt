package pl.pwr.footballcompetitionmanager.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.repository.IRepository
import retrofit2.HttpException
import timber.log.Timber
import java.lang.Exception
import java.net.SocketTimeoutException

class LoginViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _usernameFieldError = MutableLiveData<Int?>(null)
    val usernameFieldError: LiveData<Int?>
        get() = _usernameFieldError

    private val _passwordFieldError = MutableLiveData<String>(null)
    val passwordFieldError: LiveData<String>
        get() = _passwordFieldError

    private val _loginInProgress = MutableLiveData<Boolean>(false)
    val loginInProgress: LiveData<Boolean>
        get() = _loginInProgress

    private val _loginSuccessful = MutableLiveData<Boolean>(false)
    val loginSuccessful: LiveData<Boolean>
        get() = _loginSuccessful

    private val _loginError = MutableLiveData<Int>()
    val loginError: LiveData<Int>
        get() = _loginError

    private val _navigateToRegister = MutableLiveData<Boolean>(false)
    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister

    fun loginButtonClicked(username: String, password: String) {
        if (username == ""
            || password.length < Constants.MIN_PASSWORD_LENGTH || Constants.MAX_PASSWORD_LENGTH < password.length) {

            if (username == "") _usernameFieldError.value = R.string.fragment_login_empty_field_message

            if (password.length < Constants.MIN_PASSWORD_LENGTH || Constants.MAX_PASSWORD_LENGTH < password.length)
                _passwordFieldError.value = "Hasło musi zawierać od 8 do 16 znaków"
        } else {
            _loginInProgress.value = true

            viewModelScope.launch {
                try {
                    val isOperationSuccessful = repository.login(username, password)
                    joinAll()
                    if (isOperationSuccessful) {
                        Timber.d("Logowanie powiodło się - ${repository.getCurrentUser().username}")
                        _loginSuccessful.value = true
                    }
                } catch (exception: HttpException) {
                    _loginError.value = R.string.fragment_login_login_error_message
                } catch (exception: SocketTimeoutException) {
                    _loginError.value = R.string.fragment_login_login_timeout_error_message
                } catch (exception: Exception) {
                    _loginError.value = R.string.fragment_login_login_unknown_error_message
                }

                _loginInProgress.value = false
            }
        }
    }

    fun clearTextFieldErrors() {
        _usernameFieldError.value = null
        _passwordFieldError.value = null
    }

    fun loginFinished() { _loginSuccessful.value = false }

    fun navigateToRegisterScreen() { _navigateToRegister.value = true }

    fun navigationToRegisterScreenFinished() { _navigateToRegister.value = false }
}