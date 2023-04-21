package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivitySignInBinding
import com.univpm.pinpointmvvm.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewmodel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = SignInViewModel()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inSignupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.inLoginButton.setOnClickListener {
            when {
                binding.emailLogin.text.toString().isEmpty() -> Snackbar.make(
                    binding.root,
                    "Insert email",
                    Snackbar.LENGTH_SHORT
                ).show()

                binding.passwordLogin.text.toString().isEmpty() -> Snackbar.make(
                    binding.root,
                    "Insert password",
                    Snackbar.LENGTH_SHORT
                ).show()

                else -> {
                    viewmodel.loginUser(
                        binding.emailLogin.text.toString().trim(),
                        binding.passwordLogin.text.toString().trim()
                    )
                }
            }
        }



        lifecycleScope.launch {
            viewmodel.uiState.collect { state ->
                if (state.message != null) {

                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}