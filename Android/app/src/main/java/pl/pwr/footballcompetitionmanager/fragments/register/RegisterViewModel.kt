package pl.pwr.footballcompetitionmanager.fragments.register

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.EmailValidator
import timber.log.Timber

class RegisterViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _emailFieldError = MutableLiveData<Int>(null)
    val emailFieldError: LiveData<Int>
        get() = _emailFieldError

    private val _usernameFieldError = MutableLiveData<Int>(null)
    val usernameFieldError: LiveData<Int>
        get() = _usernameFieldError

    private val _passwordFieldError = MutableLiveData<String>(null)
    val passwordFieldError: LiveData<String>
        get() = _passwordFieldError

    private val _confirmPasswordFieldError = MutableLiveData<String>(null)
    val confirmPasswordFieldError: LiveData<String>
        get() = _confirmPasswordFieldError

    private val _registerInProgress = MutableLiveData<Boolean>(false)
    val registerInProgress: LiveData<Boolean>
        get() = _registerInProgress

    private val _registerSuccessful = MutableLiveData<Boolean>(false)
    val registerSuccessful: LiveData<Boolean>
        get() = _registerSuccessful

    private val _registerError = MutableLiveData<Boolean>(false)
    val registerError: LiveData<Boolean>
        get() = _registerError

    fun register(email: String, username: String, password: String, confirmPassword: String) {
        if (email == "" || !EmailValidator.isValidEmail(email)
            || username.length < Constants.MIN_USERNAME_LENGTH || Constants.MAX_USERNAME_LENGTH < username.length
            || password != confirmPassword || password.length < Constants.MIN_PASSWORD_LENGTH || Constants.MAX_PASSWORD_LENGTH < password.length) {

            Timber.d("Walidacja nie powiodła się")

            if (email == "") {
                _emailFieldError.value = R.string.fragment_register_empty_field_message
            } else if (!EmailValidator.isValidEmail(email)) {
                _emailFieldError.value = R.string.fragment_register_email_not_valid
            }

            if (username.length < Constants.MIN_USERNAME_LENGTH || Constants.MAX_USERNAME_LENGTH < username.length) {
                _usernameFieldError.value = R.string.fragment_register_username_length_not_valid
            }

            if (password.length < Constants.MIN_PASSWORD_LENGTH || Constants.MAX_PASSWORD_LENGTH < password.length) {
                _passwordFieldError.value = "Hasło musi zawierać od 8 do 16 znaków"
            } else if (password != confirmPassword) {
                _passwordFieldError.value = "Hasła są różne"
                _confirmPasswordFieldError.value = "Hasła są różne"
            }

        } else {
            _registerInProgress.value = true

            viewModelScope.launch {
                val operationSuccessful = repository.register(email, username, password, confirmPassword)
                joinAll()
                if (operationSuccessful) {
                    Timber.d("Rejestracja powiodła się")
                    _registerSuccessful.value = true
                } else {
                    Timber.d("Rejestracja nie powiodła się")
                    _registerError.value = true
                }
                _registerInProgress.value = false
            }
        }
    }

    fun clearEmailTextFieldError() { _emailFieldError.value = null }

    fun clearUsernameTextFieldError() { _usernameFieldError.value = null }

    fun clearPasswordTextFieldError() { _passwordFieldError.value = null }

    fun clearConfirmPasswordTextFieldError() { _confirmPasswordFieldError.value = null }

    fun navigationToLoginScreenFinished() { _registerSuccessful.value = false }

    //    private fun CharSequence.isValidEmail() = !isNullOrEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}