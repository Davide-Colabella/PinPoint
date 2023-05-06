package com.univpm.pinpointmvvm.view.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityAccountEditBinding
import com.univpm.pinpointmvvm.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class AccountEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountEditBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ProfileViewModel()
        var imageUri: Uri? = Uri.EMPTY
        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // Use the returned uri.
                imageUri = result.uriContent!!
                binding.profileImageAccountSettings.load(imageUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                    transformations(CircleCropTransformation())
                }
                Log.d("ImageCropper", imageUri.toString())
            } else {
                // An error occurred.
                val exception = result.error
                Log.d("ImageCropper", exception.toString())
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
            val options = CropImageOptions(
                allowFlipping = false,
                fixAspectRatio = true,
            )
            cropImage.launch(
                CropImageContractOptions(imageUri, options)
            )
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