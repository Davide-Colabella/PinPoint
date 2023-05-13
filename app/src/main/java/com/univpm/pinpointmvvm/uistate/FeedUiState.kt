package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData

data class FeedUiState(
    val isLoading : Boolean = true,
    val posts: LiveData<List<PostUiState>>? = null,
) {


    companion object{
        fun isNoLongerLoading() = FeedUiState(isLoading = false)
    }
}