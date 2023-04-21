package com.univpm.pinpointmvvm.uistate

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
) {
    companion object {
        fun loading() = SignInUiState(isLoading = true)
        fun success(message: String) = SignInUiState(message = message)
        fun error(error: String) = SignInUiState(error = error)
    }
}


