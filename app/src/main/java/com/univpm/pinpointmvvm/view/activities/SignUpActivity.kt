package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivitySignUpBinding
import com.univpm.pinpointmvvm.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewmodel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = SignUpViewModel()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upSignInButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.upSignUpButton.setOnClickListener {
            when {
                binding.fullnameSignup.text.toString().isEmpty() -> Snackbar.make(
                    binding.root,
                    "Insert full name",
                    Snackbar.LENGTH_SHORT
                ).show()
                binding.usernameSignup.text.toString().isEmpty() -> Snackbar.make(
                    binding.root,
                    "Insert username",
                    Snackbar.LENGTH_SHORT
                ).show()
                binding.emailSignup.text.toString().isEmpty() -> Snackbar.make(
                    binding.root,
                    "Insert email",
                    Snackbar.LENGTH_SHORT
                ).show()
                binding.passwordSignup.text.toString().isEmpty() -> Snackbar.make(
                    binding.root,
                    "Insert password",
                    Snackbar.LENGTH_SHORT
                ).show()
                else -> {
                    lifecycleScope.launch {
                        viewmodel.signUpUser(
                            binding.emailSignup.text.toString(),
                            binding.passwordSignup.text.toString(),
                            binding.fullnameSignup.text.toString(),
                            binding.usernameSignup.text.toString()
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.uiState.collect { state ->
                binding.progressBarSignUp.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                if (state.message != null) {
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
            }

        }
    }
}

