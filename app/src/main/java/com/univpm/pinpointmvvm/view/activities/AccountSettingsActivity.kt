package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.univpm.pinpointmvvm.databinding.ActivityAccountSettingsBinding
import com.univpm.pinpointmvvm.uistate.AccountSettingsUiState
import com.univpm.pinpointmvvm.uistate.UserUiState
import com.univpm.pinpointmvvm.viewmodel.AccountSettingsViewModel
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
                    startActivity(Intent(this@AccountSettingsActivity, SignInActivity::class.java))
                    finish()
                }
            }
        }

        binding.logoutBtn.setOnClickListener {
            viewModel.logOut()
        }
    }
}