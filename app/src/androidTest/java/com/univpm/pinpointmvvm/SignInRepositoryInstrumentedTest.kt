package com.univpm.pinpointmvvm

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.univpm.pinpointmvvm.repo.SignInRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInRepositoryInstrumentedTest {

    private val repository: SignInRepository = SignInRepository()

    @Test
    fun signIn_success() = runBlocking {
        val email = "mttgiuliani1@gmail.com"
        val password = "ciao123"

        val result = repository.signIn(email, password)

        assertEquals(Result.success(true), result)
    }

    @Test
    fun signIn_failure() = runBlocking {
        val email = "nonexistent@example.com"
        val password = "password123"

        val result = repository.signIn(email, password)

        assert(result.exceptionOrNull() is FirebaseAuthInvalidUserException)


    }
}
