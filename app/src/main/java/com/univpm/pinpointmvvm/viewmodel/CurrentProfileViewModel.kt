package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.repo.UserRepository
import com.univpm.pinpointmvvm.uistate.PostUiState
import com.univpm.pinpointmvvm.uistate.UserUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                        posts = userRepository.getPostOfUser(),
                        followers = userRepository.getFollowersOfCurrentUser(),
                        following = userRepository.getFollowingOfCurrentUser()
                    )
                }
            }
        }
    }

    fun updateProfile(username: String, name: String, bio: String, imageUri: Uri = Uri.EMPTY) {
        userRepository.updateProfile(username, name, bio, imageUri)
    }


    fun deletePost(post: PostUiState) {
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

    fun viewOnGoogleMap(postUiState: PostUiState, context: Context) {
        val locationString = "${postUiState.latitude},${postUiState.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$locationString"))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(context, intent, null)
    }

}
