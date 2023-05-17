package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.univpm.pinpointmvvm.model.repo.DatabaseSettings
import com.univpm.pinpointmvvm.model.repo.SignInRepository
import com.univpm.pinpointmvvm.uistate.SignInUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repository = SignInRepository()
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.loading()
            val result = repository.signIn(email, password)
            if (result.isSuccess) {
                _uiState.value = SignInUiState.success()
            } else{
                _uiState.value = SignInUiState.error(result.exceptionOrNull()!!.message!!)
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }
}