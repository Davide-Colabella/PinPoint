package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData
import com.univpm.pinpointmvvm.model.data.Post

data class UserUiState(
    val isFetchingProfile: Boolean = false,
    val username: String? = null,
    val fullname: String? = null,
    val bio: String? = null,
    val image: String? = null,
    val posts: LiveData<List<Post>>? = null,
)

