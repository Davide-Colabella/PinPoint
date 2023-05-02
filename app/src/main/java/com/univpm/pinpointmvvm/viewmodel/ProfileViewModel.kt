package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.uistate.UserUiState
import com.univpm.pinpointmvvm.model.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
@SuppressLint("StaticFieldLeak")
class ProfileViewModel(
) : ViewModel() {
    private val repository = UserRepository()
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        repository.listenForUserInfoChanges { s, s2, s3, s4 ->
            viewModelScope.launch {
                _uiState.update { currentState ->
                    currentState.copy(
                        fullname = s,
                        username = s2,
                        bio = s3,
                        image = s4
                    )
                }
            }
        }
    }

    fun updateProfile(username: String, name: String, bio: String) {
        repository.updateProfile(username, name, bio)
    }

    fun updateProfile(username: String, name: String, bio: String, imageUri: Uri) {
        repository.updateProfile(username, name, bio, imageUri)
    }

    fun logOut() {
        repository.logOut()
    }
}