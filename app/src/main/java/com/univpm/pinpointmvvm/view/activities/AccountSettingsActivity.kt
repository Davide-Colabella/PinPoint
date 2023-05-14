package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univpm.pinpointmvvm.databinding.ActivityAccountSettingsBinding
import com.univpm.pinpointmvvm.viewmodel.AccountSettingsViewModel
import kotlinx.coroutines.launch

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private val viewModel: AccountSettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.uiState.collect{
                if(it.isLoggedOut){
                    startActivity(Intent(this@AccountSettingsActivity, SignInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }
            }
        }

        binding.logoutBtn.setOnClickListener {
            viewModel.logOut()
        }
    }
}