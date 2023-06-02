package com.univpm.pinpointmvvm.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.repo.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private var postRepository = PostRepository.instance
    private val _postUploadSuccess = MutableStateFlow(false)
    val postUploadSuccess: StateFlow<Boolean> = _postUploadSuccess.asStateFlow()
    private val _postUploadError = MutableStateFlow("")
    val postUploadError: StateFlow<String> = _postUploadError.asStateFlow()
    private val _locationEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val locationEnabled = _locationEnabled.asStateFlow()
    private val _cameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val cameraEnabled = _cameraEnabled.asStateFlow()


    fun uploadPost(imageUri: Uri, description: String, position: LatLng) {
        viewModelScope.launch {
            val result = postRepository.uploadPost(
                imageUri = imageUri,
                description = description,
                position = position,
            )
            if (result.isSuccess) {
                _postUploadSuccess.value = true
            } else {
                _postUploadError.value = result.exceptionOrNull()!!.message!!
            }

        }

    }

    fun setLocalizationEnabled(isGranted: Boolean) {
        _locationEnabled.value = isGranted
    }

    fun setCameraEnabled(isGranted: Boolean) {
        _cameraEnabled.value = isGranted
    }


}

