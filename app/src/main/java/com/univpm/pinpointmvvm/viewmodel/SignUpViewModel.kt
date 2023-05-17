package com.univpm.pinpointmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.DatabaseSettings
import com.univpm.pinpointmvvm.model.repo.SignUpRepository
import com.univpm.pinpointmvvm.uistate.SignUpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel : ViewModel() {
    private val dbSettings : DatabaseSettings by lazy { DatabaseSettings()}
    private val repository = SignUpRepository()
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

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
