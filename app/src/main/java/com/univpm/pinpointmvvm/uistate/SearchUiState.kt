package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData
import com.univpm.pinpointmvvm.model.data.User

data class SearchUiState(
    val users: LiveData<List<User>>? = null
)