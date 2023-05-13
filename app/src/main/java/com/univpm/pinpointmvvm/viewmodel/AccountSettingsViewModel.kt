package com.univpm.pinpointmvvm.viewmodel

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.uistate.AccountSettingsUiState
import com.univpm.pinpointmvvm.view.activities.AccountSettingsActivity
import com.univpm.pinpointmvvm.view.activities.SignInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AccountSettingsUiState())
    val uiState: StateFlow<AccountSettingsUiState> = _uiState.asStateFlow()
    private val userRepository = UserRepository()

    fun logOut() {
        userRepository.logOut()
        _uiState.value = AccountSettingsUiState.logout()
    }


}