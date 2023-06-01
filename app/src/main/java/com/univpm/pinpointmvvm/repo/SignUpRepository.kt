package com.univpm.pinpointmvvm.repo

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpRepository() {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signUp(
        email: String,
        password: String,
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}