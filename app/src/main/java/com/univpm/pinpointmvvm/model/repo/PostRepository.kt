package com.univpm.pinpointmvvm.model.repo

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.univpm.pinpointmvvm.model.data.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostRepository() {

    fun uploadPost(imageUri: Uri, description: String, position : LatLng): Task<Void>? {
        var task : Task<Void>? = null
        pushPostOnDb(imageUri).addOnSuccessListener { uri ->
            val post = Post(
                imageUrl = uri.toString(),
                description = description,
                date = Date().toString(),
                userId = DatabaseSettings.auth.value?.currentUser?.uid,
                longitude = position.longitude.toString(),
                latitude = position.latitude.toString(),
            )
            task = DatabaseSettings.dbCurrentUserPosts.value?.push()?.setValue(post)
        }

        return task
    }

    private fun pushPostOnDb(imageUri: Uri): Task<Uri> {
        val formattedDateTime = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.ITALIAN).format(Date())
        val fileRef = DatabaseSettings.storagePosts.child(DatabaseSettings.auth.value?.currentUser?.uid!!)
            .child("$formattedDateTime.jpg")
        val uploadTask: UploadTask = fileRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }
    }
}