package com.univpm.pinpointmvvm.uistate

/**
 * Classe che rappresenta lo stato dell'interfaccia grafica della schermata di signup
 */
data class SignUpUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
) {
    companion object {
        fun loading() = SignUpUiState(isLoading = true)
        fun success() = SignUpUiState(isLoggedIn = true)
        fun error(toString: String) = SignUpUiState(error = toString)
    }
}

