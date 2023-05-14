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
import com.univpm.pinpointmvvm.model.utils.InputValidator
import com.univpm.pinpointmvvm.model.utils.SnackbarManager
import com.univpm.pinpointmvvm.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private val FULLNAME_ERROR = "Inserisci il nome completo. Solo caratteri alfabetici."
    private val USERNAME_ERROR = "Inserisci l'username. Non immettere degli spazi."
    private val EMAIL_ERROR = "Inserisci un'e-mail valida"
    private val PASSWORD_ERROR = "Inserisci una password di almeno 6 caratteri"

    private lateinit var viewBinding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private val inputValidator: InputValidator<ActivitySignUpBinding> by lazy {
        InputValidator(
            viewBinding
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        signInButtonListener()
        signUpButtonListener()

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    disableUI()
                } else {
                    enableUI()
                }
                if (state.isLoggedIn) {
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                } else if (state.error != null) {
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

    private fun signUpButtonListener() {
        viewBinding.upSignUpButton.setOnClickListener {
            hideKeyboard()
            val fullname = viewBinding.fullnameSignup.editText?.text.toString().trim()
            val username = viewBinding.usernameSignup.editText?.text.toString().trim()
            val email = viewBinding.emailSignup.editText?.text.toString().trim()
            val password = viewBinding.passwordSignup.editText?.text.toString().trim()

            when {
                !inputValidator.isValidFullName(fullname) -> SnackbarManager.onWarning(
                    FULLNAME_ERROR,
                    this,
                    viewBinding.root
                )

                !inputValidator.isValidUsername(username) -> SnackbarManager.onWarning(
                    USERNAME_ERROR,
                    this,
                    viewBinding.root
                )

                !inputValidator.isValidEmail(email) -> SnackbarManager.onWarning(
                    EMAIL_ERROR,
                    this,
                    viewBinding.root
                )

                !inputValidator.isValidPassword(password) -> SnackbarManager.onWarning(
                    PASSWORD_ERROR,
                    this,
                    viewBinding.root
                )

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

    private fun signInButtonListener() {
        viewBinding.upSignInButton.setOnClickListener {
            hideKeyboard()
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }


    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

