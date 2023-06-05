package com.univpm.pinpointmvvm.uistate

/**
 * Classe che rappresenta lo stato dell'interfaccia grafica della schermata delle impostazioni
 */
data class AccountSettingsUiState(
    var isLoggedOut : Boolean = false
) {
    companion object{
        fun logout() = AccountSettingsUiState(isLoggedOut = true)
    }
}