package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.data.Post
import com.univpm.pinpointmvvm.model.repo.DatabaseSettings
import com.univpm.pinpointmvvm.uistate.UserUiState
import com.univpm.pinpointmvvm.model.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
class CurrentProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _postDeleteSuccess = MutableStateFlow(false)
    val postDeleteSuccess: StateFlow<Boolean> = _postDeleteSuccess.asStateFlow()
    private val _postDeleteError = MutableStateFlow("")
    val postDeleteError: StateFlow<String> = _postDeleteError.asStateFlow()

    init {

        userRepository.listenForUserInfoChanges { fullname, username, bio, image ->
            viewModelScope.launch {
                _uiState.update { currentState ->
                    currentState.copy(
                        fullname = fullname,
                        username = username,
                        bio = bio,
                        image = image,
                        posts = userRepository.getPostOfUser()
                    )
                }
            }
        }
    }

    fun updateProfile(username: String, name: String, bio: String, imageUri: Uri = Uri.EMPTY) {
        userRepository.updateProfile(username, name, bio, imageUri)
    }

    fun logOut() {
        userRepository.logOut()
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    userRepository.deletePost(post)
                }
                _postDeleteSuccess.value = true
            } catch (e: Exception) {
                _postDeleteError.value = e.message.toString()
            }
        }
    }

}
