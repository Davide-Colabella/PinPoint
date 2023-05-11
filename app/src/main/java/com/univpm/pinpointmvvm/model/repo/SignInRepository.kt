package com.univpm.pinpointmvvm.model.repo

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SignInRepository {

    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        val firebaseAuth = FirebaseAuth.getInstance()
                        DatabaseSettings.auth.value = firebaseAuth
                    }
                }
            Result.success("Sign In Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}