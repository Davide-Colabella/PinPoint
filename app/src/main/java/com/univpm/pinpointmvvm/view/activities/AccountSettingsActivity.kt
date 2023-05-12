package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.univpm.pinpointmvvm.databinding.ActivityAccountSettingsBinding
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private val viewModel: CurrentProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutBtn.setOnClickListener {
            viewModel.logOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}