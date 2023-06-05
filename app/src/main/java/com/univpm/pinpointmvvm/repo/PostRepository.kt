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

/**
 * Classe che rappresenta il repository per la gestione dei post
 */
class PostRepository {
    companion object {
        val instance = PostRepository()
    }

    private val dbSettings = DatabaseSettings()

    /**
     * Funzione che carica un post
     * @param imageUri uri dell'immagine
     * @param description descrizione del post
     * @param position coordinate del post
     * @return risultato dell'operazione
     * @see Result
     */
    suspend fun uploadPost(
        imageUri: Uri,
        description: String,
        position: LatLng,
    ): Result<Boolean> = withContext(Dispatchers.IO) {
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

    /**
     * Funzione che carica un post sul database
     * @param imageUri uri dell'immagine
     * @return uri dell'immagine
     */
    private fun pushPostOnDb(imageUri: Uri): Task<Uri> {
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
        val formattedDate = format.format(date)
        val fileRef =
            dbSettings.storagePosts.child(dbSettings.auth.uid!!).child("$formattedDate.jpg")

        val uploadTask = fileRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }
    }
}