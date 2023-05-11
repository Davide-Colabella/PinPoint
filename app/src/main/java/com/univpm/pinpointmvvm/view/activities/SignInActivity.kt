package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.univpm.pinpointmvvm.databinding.ActivitySignInBinding
import com.univpm.pinpointmvvm.model.repo.DatabaseSettings
import com.univpm.pinpointmvvm.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySignInBinding
    private var viewModel = SignInViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.inSignupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        viewBinding.inLoginButton.setOnClickListener {
            when {
                viewBinding.emailLogin.text.toString().isEmpty() -> Snackbar.make(
                    viewBinding.root,
                    "Insert email",
                    Snackbar.LENGTH_SHORT
                ).show()

                viewBinding.passwordLogin.text.toString().isEmpty() -> Snackbar.make(
                    viewBinding.root,
                    "Insert password",
                    Snackbar.LENGTH_SHORT
                ).show()

                else -> {
                    viewModel.loginUser(
                        viewBinding.emailLogin.text.toString().trim(),
                        viewBinding.passwordLogin.text.toString().trim()
                    )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.message != null) {
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else if (state.error != null) {
                    Snackbar.make(viewBinding.root, state.error, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.isLoggedIn()) {
            val firebaseAuth = FirebaseAuth.getInstance()
            DatabaseSettings.auth.value = firebaseAuth
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}