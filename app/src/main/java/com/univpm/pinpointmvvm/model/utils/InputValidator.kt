package com.univpm.pinpointmvvm.model.utils

import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

class InputValidator<T : ViewBinding>(
    private val viewBinding: T
) {
    fun snackbarError(message: String) {
        Snackbar.make(
            viewBinding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    // Verifica se il fullname contiene solo lettere dell'alfabeto, spazi e non è vuoto
    fun isValidFullName(fullName: String): Boolean {
        val regex = Regex("^[a-zA-Z\\s]+$")
        return fullName.isNotBlank() && regex.matches(fullName)
    }

    // Verifica se lo username contiene solo caratteri alfanumerici, caratteri speciali, non contiene spazi e non è vuoto
    fun isValidUsername(username: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9\\p{Punct}]+$")
        return username.isNotBlank() && !username.contains(" ") && regex.matches(username)
    }

    // Verifica se l'email è nel formato corretto e non è vuota
    fun isValidEmail(email: String): Boolean {
        val regex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return email.isNotBlank() && regex.matches(email)
    }

    // Verifica se la password non è vuota
    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 6
    }

}