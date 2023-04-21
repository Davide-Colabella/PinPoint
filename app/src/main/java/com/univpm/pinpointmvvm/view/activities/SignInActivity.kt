package com.univpm.pinpointmvvm.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univpm.pinpointmvvm.databinding.ActivitySignInBinding
import com.univpm.pinpointmvvm.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewmodel: SignInViewModel
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = SignInViewModel()
        sharedPref = getPreferences(Context.MODE_PRIVATE)
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

        if (sharedPref.getBoolean("isLogged", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            viewmodel.uiState.collect { state ->
                if (state.message != null) {
                    sharedPref.edit().putBoolean("isLogged", true).apply()
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}