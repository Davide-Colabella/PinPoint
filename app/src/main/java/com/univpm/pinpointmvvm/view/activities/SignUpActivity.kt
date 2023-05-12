package com.univpm.pinpointmvvm.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.univpm.pinpointmvvm.databinding.ActivitySignUpBinding
import com.univpm.pinpointmvvm.model.utils.InputValidator
import com.univpm.pinpointmvvm.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by lazy { ViewModelProvider(this)[SignUpViewModel::class.java] }
    private val inputValidator: InputValidator<ActivitySignUpBinding> by lazy { InputValidator(viewBinding) }
    private val errorMessages = mapOf(
        "fullname" to "Inserisci il nome completo. Solo caratteri alfabetici.",
        "username" to "Inserisci l'username. Non immettere degli spazi.",
        "email" to "Inserisci un'e-mail valida",
        "password" to "Inserisci una password"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        signInButtonListener()
        signUpButtonListener()



        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                viewBinding.progressBarSignUp.visibility =
                    if (state.isLoading) View.VISIBLE else View.GONE
                if (state.isLoggedIn) {
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }else if(state.error != null){
                    inputValidator.snackbarError(state.error)
                }
            }

        }
    }

    private fun signUpButtonListener() {
        viewBinding.upSignUpButton.setOnClickListener {
            hideKeyboard()
            val fullname = viewBinding.fullnameSignup.text.toString()
            val username = viewBinding.usernameSignup.text.toString()
            val email = viewBinding.emailSignup.text.toString()
            val password = viewBinding.passwordSignup.text.toString()

            when {
                !inputValidator.isValidFullName(fullname) -> inputValidator.snackbarError(
                    errorMessages["fullname"]!!
                )

                !inputValidator.isValidUsername(username) -> inputValidator.snackbarError(
                    errorMessages["username"]!!
                )

                !inputValidator.isValidEmail(email) -> inputValidator.snackbarError(
                    errorMessages["email"]!!
                )

                !inputValidator.isValidPassword(password) -> inputValidator.snackbarError(
                    errorMessages["password"]!!
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

