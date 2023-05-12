package com.univpm.pinpointmvvm.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.univpm.pinpointmvvm.databinding.ActivitySignInBinding
import com.univpm.pinpointmvvm.model.repo.DatabaseSettings
import com.univpm.pinpointmvvm.model.utils.InputValidator
import com.univpm.pinpointmvvm.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySignInBinding
    private val viewModel: SignInViewModel by lazy { ViewModelProvider(this)[SignInViewModel::class.java] }
    private val inputValidator: InputValidator<ActivitySignInBinding> by lazy {
        InputValidator(
            viewBinding
        )
    }
    private val errorMessages = mapOf(
        "email" to "Inserisci un'e-mail valida",
        "password" to "Inserisci una password",
        "login" to "Login non riuscito"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        signInButtonListener()
        signUpButtonListener()

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoggedIn) {
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else if (state.error != null) {
                    inputValidator.snackbarError(state.error)
                }
            }
        }
    }

    private fun signUpButtonListener() {
        viewBinding.inLoginButton.setOnClickListener {
            hideKeyboard()
            val email = viewBinding.emailLogin.text.toString()
            val password = viewBinding.passwordLogin.text.toString()
            when {
                !inputValidator.isValidEmail(email) -> inputValidator.snackbarError(
                    errorMessages["email"]!!
                )

                !inputValidator.isValidPassword(password) -> inputValidator.snackbarError(
                    errorMessages["password"]!!
                )

                else -> {
                    viewModel.loginUser(
                        email = email,
                        password = password,
                    )
                }
            }
        }
    }

    private fun signInButtonListener() {
        viewBinding.inSignupButton.setOnClickListener {
            hideKeyboard()
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.isLoggedIn()) {
            DatabaseSettings.auth.value = FirebaseAuth.getInstance()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}