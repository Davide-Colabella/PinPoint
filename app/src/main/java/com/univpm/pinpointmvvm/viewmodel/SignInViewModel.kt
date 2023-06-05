package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.repo.SignInRepository
import com.univpm.pinpointmvvm.uistate.SignInUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione del login
 */
class SignInViewModel : ViewModel() {
    // Repository
    private val repository = SignInRepository()

    // StateFlow per la gestione dello stato del login
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    /**
     * Metodo per il login
     * @param email email dell'utente
     * @param password password dell'utente
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.loading()
            val result = repository.signIn(email, password)
            if (result.isSuccess) {
                _uiState.value = SignInUiState.success()
            } else {
                _uiState.value = SignInUiState.error(result.exceptionOrNull()!!.message!!)
            }
        }
    }

    /**
     * Metodo per il controllo se l'utente è loggato
     * @return true se l'utente è loggato, false altrimenti
     */
    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }
}