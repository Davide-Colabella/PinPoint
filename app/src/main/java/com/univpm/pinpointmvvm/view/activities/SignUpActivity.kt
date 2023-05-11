package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.databinding.ActivitySignUpBinding
import com.univpm.pinpointmvvm.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding
    private var viewModel = SignUpViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.upSignInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        viewBinding.upSignUpButton.setOnClickListener {
            when {
                viewBinding.fullnameSignup.text.toString().isEmpty() -> Snackbar.make(
                    viewBinding.root,
                    "Insert full name",
                    Snackbar.LENGTH_SHORT
                ).show()
                viewBinding.usernameSignup.text.toString().isEmpty() -> Snackbar.make(
                    viewBinding.root,
                    "Insert username",
                    Snackbar.LENGTH_SHORT
                ).show()
                viewBinding.emailSignup.text.toString().isEmpty() -> Snackbar.make(
                    viewBinding.root,
                    "Insert email",
                    Snackbar.LENGTH_SHORT
                ).show()
                viewBinding.passwordSignup.text.toString().isEmpty() -> Snackbar.make(
                    viewBinding.root,
                    "Insert password",
                    Snackbar.LENGTH_SHORT
                ).show()
                else -> {
                    lifecycleScope.launch {
                        viewModel.signUpUser(
                            viewBinding.emailSignup.text.toString(),
                            viewBinding.passwordSignup.text.toString(),
                            viewBinding.fullnameSignup.text.toString(),
                            viewBinding.usernameSignup.text.toString()
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                viewBinding.progressBarSignUp.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                if (state.message != null) {
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
            }

        }
    }
}

