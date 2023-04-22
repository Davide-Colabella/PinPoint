package com.univpm.pinpointmvvm.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityAccountSettingsBinding
import com.univpm.pinpointmvvm.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ProfileViewModel()
        var imageUri: Uri? = null
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
                binding.profileImageAccountSettings.load(imageUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_profile)
                    transformations(CircleCropTransformation())
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.saveAccountSettingsBtn.setOnClickListener {
            if (imageUri != null) {
                viewModel.updateProfile(
                    binding.accountNameAccountSettings.text.toString(),
                    binding.accountUsernameAccountSettings.text.toString(),
                    binding.accountBioAccountSettings.text.toString(),
                    imageUri!!
                )
            } else {
                viewModel.updateProfile(
                    binding.accountUsernameAccountSettings.text.toString(),
                    binding.accountNameAccountSettings.text.toString(),
                    binding.accountBioAccountSettings.text.toString()
                )
            }
            finish()
        }

        binding.closeAccountSettingsBtn.setOnClickListener {
            finish()
        }

        binding.changeProfileImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.logoutBtn.setOnClickListener {
            viewModel.logOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.accountUsernameAccountSettings.setText(uiState.username)
                binding.accountNameAccountSettings.setText(uiState.fullname)
                binding.accountBioAccountSettings.setText(uiState.bio)
                binding.profileImageAccountSettings.load(uiState.image) {
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}