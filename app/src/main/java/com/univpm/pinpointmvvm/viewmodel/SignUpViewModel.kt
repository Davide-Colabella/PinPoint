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
            val result = repository.signUp(email, password, fullname, username, bio = "Hi there! I'm using Pinpoint.", profilePic = "https://firebasestorage.googleapis.com/v0/b/pinpointmvvm.appspot.com/o/Default%20Images%2Fprofile_picture.png?alt=media&token=b27db6c2-9178-405e-ac23-a8e1e7fa0e28")
            if (result.isSuccess) {
                _uiState.value = SignUpUiState.success(result.getOrNull()!!)
            } else {
                _uiState.value = SignUpUiState.error(result.exceptionOrNull()!!.message!!)
            }
        }
    }

}
