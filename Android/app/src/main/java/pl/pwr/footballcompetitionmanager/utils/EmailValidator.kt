package pl.pwr.footballcompetitionmanager.utils

import androidx.core.util.PatternsCompat

object EmailValidator {
    fun isValidEmail(email: String) = !email.isNullOrEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
}