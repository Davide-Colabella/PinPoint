package com.univpm.pinpointmvvm.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.repo.PostRepository
import com.univpm.pinpointmvvm.model.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel : ViewModel() {

    private var postRepository = PostRepository()
    private val _postUploadSuccess = MutableStateFlow(false)
    val postUploadSuccess: StateFlow<Boolean> = _postUploadSuccess.asStateFlow()
    private val _postUploadError = MutableStateFlow("")
    val postUploadError: StateFlow<String> = _postUploadError.asStateFlow()

    fun uploadPost(imageUri: Uri, description: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Default) {
                    postRepository.uploadPost(
                        imageUri = imageUri,
                        description = description
                    )
                }
                _postUploadSuccess.value = true
            } catch (e: Exception) {
                _postUploadError.value = e.message.toString()
            }
        }

    }


}

