package com.univpm.pinpointmvvm.uistate

/**
 * Classe che rappresenta lo stato dell'interfaccia grafica della schermata di login
 */
data class SignInUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
) {
    companion object {
        fun loading() = SignInUiState(isLoading = true)
        fun success() = SignInUiState(isLoggedIn = true)
        fun error(toString: String) = SignInUiState(error = toString)
    }
}


