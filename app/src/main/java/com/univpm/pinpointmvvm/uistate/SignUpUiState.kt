package com.univpm.pinpointmvvm.uistate

data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
) {
    companion object {
        fun loading() = SignUpUiState(isLoading = true)
        fun success(message: String) = SignUpUiState(message = message)
        fun error(error: String) = SignUpUiState(error = error)
    }
}

