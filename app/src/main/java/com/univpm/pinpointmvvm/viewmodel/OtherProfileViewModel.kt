package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.uistate.PostUiState
import com.univpm.pinpointmvvm.uistate.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtherProfileViewModel(user: User) : ViewModel() {


    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    private val userRepository = UserRepository()

    init {
        userRepository.listenForUserInfoChanges(user) { fullname, username, bio, image ->
            viewModelScope.launch {
                _uiState.update { currentState ->
                    currentState.copy(
                        fullname = fullname,
                        username = username,
                        bio = bio,
                        image = image,
                        posts = userRepository.getPostOfUser(user),
                    )
                }
            }
        }
    }

    fun viewOnGoogleMap(it: PostUiState, context : Context) {
        val locationString = "${it.latitude},${it.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$locationString"))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(context,intent, null)
    }

    class OtherProfileViewModelFactory(
        private val user: User,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OtherProfileViewModel::class.java)) {
                return OtherProfileViewModel(user) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}