package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.repo.SignUpRepository
import com.univpm.pinpointmvvm.uistate.SignUpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val repository = SignUpRepository()
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun signUpUser(email: String, password: String, fullname: String, username: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.loading()
            val result = repository.signUp(email, password, fullname, username, bio = "Hi there! I'm using Pinpoint.", profilePic = "https://firebasestorage.googleapis.com/v0/b/pinpointmvvm.appspot.com/o/Default%20Images%2FProfilePicture.png?alt=media&token=780391e3-37ee-4352-8367-f4c08b0f809d")
            if (result.isSuccess) {
                _uiState.value = SignUpUiState.success(result.getOrNull()!!)
            } else {
                _uiState.value = SignUpUiState.error(result.exceptionOrNull()!!.message!!)
            }
        }
    }

}
