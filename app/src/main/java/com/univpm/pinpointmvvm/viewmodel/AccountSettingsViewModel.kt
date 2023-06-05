package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.univpm.pinpointmvvm.repo.UserRepository
import com.univpm.pinpointmvvm.uistate.AccountSettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel per la gestione delle impostazioni dell'account
 */
class AccountSettingsViewModel : ViewModel() {
    // StateFlow per la gestione dello stato dell'account
    private val _uiState = MutableStateFlow(AccountSettingsUiState())
    val uiState: StateFlow<AccountSettingsUiState> = _uiState.asStateFlow()

    // Repository
    private val userRepository = UserRepository()

    /**
     * Metodo per il logout
     */
    fun logOut() {
        userRepository.logOut()
        _uiState.value = AccountSettingsUiState.logout()
    }


}