package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.repo.FeedRepository
import com.univpm.pinpointmvvm.uistate.FeedUiState
import com.univpm.pinpointmvvm.uistate.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    private val feedRepository = FeedRepository()

    init {
        viewModelScope.launch {
            _uiState.update {state ->
                state.copy(
                    posts = feedRepository.getAllPosts(),
                )
            }
        }
    }




}