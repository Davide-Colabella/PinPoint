package com.univpm.pinpointmvvm.repo

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.univpm.pinpointmvvm.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostRepository {
    companion object {
        val instance = PostRepository()
    }

    private val dbSettings = DatabaseSettings()

    /*fun uploadPost(imageUri: Uri, description: String, position: LatLng): Task<Void>? {
        var task: Task<Void>? = null
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
    }*/
    suspend fun uploadPost(imageUri: Uri, description: String, position: LatLng): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val date = Date()
                val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                val formattedDate = format.format(date)


                pushPostOnDb(imageUri).addOnSuccessListener { uri ->
                    val post = Post(
                        imageUrl = uri.toString(),
                        description = description,
                        date = formattedDate,
                        userId = dbSettings.auth.uid,
                        longitude = position.longitude.toString(),
                        latitude = position.latitude.toString(),
                    )
                    dbSettings.dbCurrentUserPosts.push().setValue(post)
                }
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun pushPostOnDb(imageUri: Uri): Task<Uri> {
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
        val formattedDate = format.format(date)
        val fileRef = dbSettings.storagePosts
            .child(dbSettings.auth.uid!!)
            .child("$formattedDate.jpg")

        val uploadTask = fileRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }
    }
}