package com.univpm.pinpointmvvm.model.repo

import android.provider.ContactsContract.Data
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

class SignInRepository {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(email: String, password: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}