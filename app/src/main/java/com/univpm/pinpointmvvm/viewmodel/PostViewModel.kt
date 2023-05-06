package com.univpm.pinpointmvvm.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.univpm.pinpointmvvm.model.repo.UserRepository

class PostViewModel : ViewModel() {

    private var userRepository = UserRepository()

    fun uploadPost(imageUri: Uri, description: String) {
        userRepository.uploadPost(imageUri, description)
    }

}