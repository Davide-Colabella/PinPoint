package com.univpm.pinpointmvvm.uistate

data class UserUiState(
    val isFetchingProfile: Boolean = false,
    val username: String? = null,
    val fullname: String? = null,
    val bio: String? = null,
    val image: String? = null
)

