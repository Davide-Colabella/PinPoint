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

/**
 * ViewModel per la gestione dei post
 */
class PostViewModel : ViewModel() {
    // Repository
    private var postRepository = PostRepository.instance

    // StateFlow per la gestione dello stato del post
    private val _postUploadSuccess = MutableStateFlow(false)
    val postUploadSuccess: StateFlow<Boolean> = _postUploadSuccess.asStateFlow()
    private val _postUploadError = MutableStateFlow("")
    val postUploadError: StateFlow<String> = _postUploadError.asStateFlow()

    // StateFlow per la gestione dello stato della localizzazione
    private val _locationEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val locationEnabled = _locationEnabled.asStateFlow()

    // StateFlow per la gestione dello stato della fotocamera
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

    /**
     * Metodo per impostare lo stato della localizzazione
     * @param isGranted stato della localizzazione
     */
    fun setLocalizationEnabled(isGranted: Boolean) {
        _locationEnabled.value = isGranted
    }

    /**
     * Metodo per impostare lo stato della fotocamera
     * @param isGranted stato della fotocamera
     */
    fun setCameraEnabled(isGranted: Boolean) {
        _cameraEnabled.value = isGranted
    }


}

