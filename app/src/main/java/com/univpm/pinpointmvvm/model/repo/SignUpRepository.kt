package com.univpm.pinpointmvvm.model.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.univpm.pinpointmvvm.model.data.User
import kotlinx.coroutines.tasks.await

class SignUpRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    suspend fun signUp(email: String, password: String, fullname: String, username: String, bio: String, profilePic: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            //da qui
            val firebaseAuth = FirebaseAuth.getInstance()
            DatabaseSettings.auth.value = firebaseAuth
            //a  qui
            val currentUser = auth.currentUser!!
            val userId = currentUser.uid
            val user = User(userId, fullname, username, email, profilePic, bio)
            database.child("users").child(userId).setValue(user).await()
            Result.success("Sign Up Successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}