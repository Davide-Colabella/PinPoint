package com.univpm.pinpointmvvm.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.univpm.pinpointmvvm.databinding.ActivitySignInBinding
import com.univpm.pinpointmvvm.model.repo.DatabaseSettings
import com.univpm.pinpointmvvm.model.utils.InputValidator
import com.univpm.pinpointmvvm.model.utils.SnackbarManager
import com.univpm.pinpointmvvm.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    companion object{
        private const val EMAIL_ERROR = "Inserisci un'e-mail valida"
        private const val PASSWORD_ERROR = "Inserisci una password"
    }

    private lateinit var viewBinding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()
    private val inputValidator = InputValidator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.inLoginButton.setOnClickListener(signInClick())
        viewBinding.inSignupButton.setOnClickListener(signUpClick())

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) disableUi() else enableUi()
                if (state.isLoggedIn) {
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                }
                if (state.error != null) {
                    enableUi()
                    SnackbarManager.onFailure(state.error, this@SignInActivity, viewBinding.root)
                }
            }
        }
    }

    private fun enableUi() {
        viewBinding.apply {
            progressBarSignIn.visibility = View.GONE
            overlay.overlay.visibility = View.GONE
            emailLogin.isEnabled = true
            passwordLogin.isEnabled = true
            inLoginButton.isEnabled = true
            inSignupButton.isEnabled = true
        }
    }

    private fun disableUi() {
        viewBinding.apply {
            progressBarSignIn.visibility = View.VISIBLE
            overlay.overlay.visibility = View.VISIBLE
            emailLogin.isEnabled = false
            passwordLogin.isEnabled = false
            inLoginButton.isEnabled = false
            inSignupButton.isEnabled = false
        }
    }


    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun signInClick(): View.OnClickListener {
        return View.OnClickListener {
            hideKeyboard()
            val email = viewBinding.emailLogin.editText?.text.toString()
            val password = viewBinding.passwordLogin.editText?.text.toString()
            if (inputValidator.isValidEmail(email)) {
                if (inputValidator.isValidPassword(password)) {
                    viewModel.login(email, password)
                } else {
                    viewBinding.passwordLogin.error = PASSWORD_ERROR
                }
            } else {
                viewBinding.emailLogin.error = EMAIL_ERROR
            }
        }
    }

    private fun signUpClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}