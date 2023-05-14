package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData

data class FeedUiState(
    val isLoading : Boolean = false,
    val isLoaded: Boolean = false,
    val error: String? = null,
    val posts: LiveData<List<PostUiState>>? = null,
) {

    companion object {
        fun loading() = FeedUiState(isLoading = true)
        fun success() = FeedUiState(isLoaded = true, isLoading = false)
        fun error(toString: String) = FeedUiState(error = toString)
    }
}