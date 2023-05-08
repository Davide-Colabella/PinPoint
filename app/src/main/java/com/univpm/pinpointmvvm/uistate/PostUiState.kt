package com.univpm.pinpointmvvm.uistate

data class PostUiState(
    val imageUrl: String? = null,
    var userPic: String? = null,
    var username: String? = null,
    val description: String? = null,
    val date: String? = null
)