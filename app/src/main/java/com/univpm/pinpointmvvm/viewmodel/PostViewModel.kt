package com.univpm.pinpointmvvm.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.univpm.pinpointmvvm.model.repo.UserRepository

class PostViewModel : ViewModel() {

    private var userRepository = UserRepository()

    fun uploadPost(imageUri: Uri) {
        userRepository.uploadPost(imageUri)
    }

}