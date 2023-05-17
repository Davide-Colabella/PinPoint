package com.univpm.pinpointmvvm.model.utils

class InputValidator {
    // Verifica se il fullname contiene solo lettere dell'alfabeto, spazi e non è vuoto
    fun isValidFullName(fullName: String): Boolean {
        val regex = Regex("^[\\p{L}\\s]+$")
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
        return email.isNotBlank() && !email.contains(" ") && regex.matches(email)
    }

    // Verifica se la password non è vuota e non contiene spazi
    fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty() && !password.contains(" ") && password.length >= 6
    }

}