package com.univpm.pinpointmvvm

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.univpm.pinpointmvvm.viewmodel.SignUpViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class SignUpTest {

    @MockK
    private lateinit var mockDatabase: FirebaseDatabase

    @MockK
    private lateinit var mockDatabaseReference: DatabaseReference

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic(FirebaseDatabase::class)

        val app = FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        val auth = app?.let { FirebaseAuth.getInstance(it) }

        every { FirebaseDatabase.getInstance() } returns mockDatabase
        every { mockDatabase.reference } returns mockDatabaseReference
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSignUp() = runBlockingTest {
        val viewModel = SignUpViewModel()

        // Configura il comportamento desiderato per la simulazione del database
        val mockDatabaseChild = mockk<DatabaseReference>()
        every { mockDatabaseReference.child("users") } returns mockDatabaseChild
        every { mockDatabaseChild.child(any()) } returns mockDatabaseChild
        every { mockDatabaseChild.setValue(any()) } returns mockk()

        launch {
            viewModel.signUpUser(
                email = "prova@email.com",
                password = "password",
                fullname = "Nome Cognome",
                username = "nomecognome"
            )
        }

        // Verifica se sono state eseguite le operazioni corrette sul database
        verify(exactly = 1) { mockDatabaseReference.child("users") }
        verify(exactly = 1) { mockDatabaseChild.child(any()) }
        verify(exactly = 1) { mockDatabaseChild.setValue(any()) }
    }
}
