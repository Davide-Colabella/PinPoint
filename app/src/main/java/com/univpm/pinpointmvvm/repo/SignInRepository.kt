package com.univpm.pinpointmvvm.repo

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Classe che rappresenta il repository per la gestione del login
 */
class SignInRepository {

    private val auth = FirebaseAuth.getInstance()

    /**
     * Funzione che effettua il login
     * @param email email dell'utente
     * @param password password dell'utente
     * @return risultato dell'operazione
     * @see Result
     */
    suspend fun signIn(email: String, password: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Funzione che determina se l'utente è loggato
     * @return true se l'utente è loggato, false altrimenti
     */
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}