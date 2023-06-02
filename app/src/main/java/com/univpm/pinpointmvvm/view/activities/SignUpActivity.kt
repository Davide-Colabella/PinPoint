package com.univpm.pinpointmvvm.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univpm.pinpointmvvm.databinding.ActivitySignUpBinding
import com.univpm.pinpointmvvm.utils.InputValidator
import com.univpm.pinpointmvvm.utils.SnackbarManager
import com.univpm.pinpointmvvm.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    companion object {
        private const val FULLNAME_ERROR = "Inserisci il nome completo. Solo caratteri alfabetici."
        private const val USERNAME_ERROR = "Inserisci l'username. Non immettere degli spazi."
        private const val EMAIL_ERROR = "Inserisci un'e-mail valida"
        private const val PASSWORD_ERROR = "Inserisci una password di almeno 6 caratteri"
    }

    private lateinit var viewBinding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private val inputValidator = InputValidator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.upSignUpButton.setOnClickListener(signUpClick())
        viewBinding.upSignInButton.setOnClickListener(signInClick())

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) disableUI() else enableUI()
                if (state.isLoggedIn) {
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
                if (state.error != null) {
                    enableUI()
                    SnackbarManager.onFailure(state.error, this@SignUpActivity, viewBinding.root)
                }
            }

        }
    }

    private fun enableUI() {
        viewBinding.apply {
            progressBarSignUp.visibility = View.GONE
            overlay.overlay.visibility = View.GONE
            fullnameSignup.isEnabled = true
            usernameSignup.isEnabled = true
            emailSignup.isEnabled = true
            passwordSignup.isEnabled = true
            upSignInButton.isEnabled = true
            upSignUpButton.isEnabled = true
        }
    }

    private fun disableUI() {
        viewBinding.apply {
            progressBarSignUp.visibility = View.VISIBLE
            overlay.overlay.visibility = View.VISIBLE
            fullnameSignup.isEnabled = false
            usernameSignup.isEnabled = false
            emailSignup.isEnabled = false
            passwordSignup.isEnabled = false
            upSignInButton.isEnabled = false
            upSignUpButton.isEnabled = false
        }
    }

    private fun signInClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }
    }

    private fun signUpClick(): View.OnClickListener {
        return View.OnClickListener {
            hideKeyboard()
            val fullname = viewBinding.fullnameSignup.editText?.text.toString()
            val username = viewBinding.usernameSignup.editText?.text.toString()
            val email = viewBinding.emailSignup.editText?.text.toString()
            val password = viewBinding.passwordSignup.editText?.text.toString()

            when {
                !inputValidator.isValidFullName(fullname) -> viewBinding.fullnameSignup.error =
                    FULLNAME_ERROR

                !inputValidator.isValidUsername(username) -> viewBinding.usernameSignup.error =
                    USERNAME_ERROR

                !inputValidator.isValidEmail(email) -> viewBinding.emailSignup.error = EMAIL_ERROR

                !inputValidator.isValidPassword(password) -> viewBinding.passwordSignup.error =
                    PASSWORD_ERROR

                else -> {
                    lifecycleScope.launch {
                        viewModel.signUpUser(
                            fullname = fullname,
                            username = username,
                            email = email,
                            password = password
                        )
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

