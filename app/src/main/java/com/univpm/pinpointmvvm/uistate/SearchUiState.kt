package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData
import com.univpm.pinpointmvvm.model.User

data class SearchUiState(
    val users: LiveData<List<User>>? = null
)