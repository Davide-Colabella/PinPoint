package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.User
import com.univpm.pinpointmvvm.repo.DatabaseSettings
import com.univpm.pinpointmvvm.repo.SignUpRepository
import com.univpm.pinpointmvvm.uistate.SignUpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel per la gestione della registrazione
 */
class SignUpViewModel : ViewModel() {
    // DatabaseSettings
    private val dbSettings: DatabaseSettings by lazy { DatabaseSettings() }

    // Repository
    private val repository = SignUpRepository()

    // StateFlow per la gestione dello stato della registrazione
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    /**
     * Metodo per la registrazione di un utente
     * @param email email dell'utente
     * @param password password dell'utente
     * @param fullname nome completo dell'utente
     * @param username username dell'utente
     */
    fun signUpUser(email: String, password: String, fullname: String, username: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.loading()
            val result = repository.signUp(email, password)
            if (result.isSuccess) {
                User(
                    uid = dbSettings.auth.uid!!,
                    fullname = fullname,
                    username = username,
                    email = email
                ).apply {
                    dbSettings.dbCurrentUser.setValue(this).await()
                }
                _uiState.value = SignUpUiState.success()
            } else {
                _uiState.value = SignUpUiState.error(result.exceptionOrNull()!!.message!!)
            }
        }
    }

}
