package com.univpm.pinpointmvvm.repo

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Classe che rappresenta il repository per la gestione della registrazione
 */
class SignUpRepository() {
    private val auth = FirebaseAuth.getInstance()

    /**
     * Funzione che effettua la registrazione
     * @param email email dell'utente
     * @param password password dell'utente
     * @return risultato dell'operazione
     * @see Result
     */
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