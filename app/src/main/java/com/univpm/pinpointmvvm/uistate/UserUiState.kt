package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData

data class UserUiState(
    val isFetchingProfile: Boolean = false,
    val username: String? = null,
    val fullname: String? = null,
    val bio: String? = null,
    val image: String? = null,
    val posts: LiveData<List<PostUiState>>? = null,
    val followers: LiveData<Int>? = null,
    val following: LiveData<Int>? = null,
)

