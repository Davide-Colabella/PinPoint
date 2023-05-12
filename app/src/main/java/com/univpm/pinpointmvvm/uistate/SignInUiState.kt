package com.univpm.pinpointmvvm.uistate

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


