package com.univpm.pinpointmvvm.uistate

data class AccountSettingsUiState(
    var isLoggedOut : Boolean = false
) {
    companion object{
        fun logout() = AccountSettingsUiState(isLoggedOut = true)
    }
}