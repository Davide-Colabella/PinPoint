package com.univpm.pinpointmvvm.view.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityAccountEditBinding
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel
import kotlinx.coroutines.launch

class AccountEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountEditBinding
    private lateinit var viewModel :  CurrentProfileViewModel
    private var imageUri: Uri? = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CurrentProfileViewModel::class.java]

        setUpUi()
        val result = cropImage()
        changeProfileImage(result)
        save()
        discard()
    }

    private fun discard() {
        binding.closeAccountSettingsBtn.setOnClickListener {
            finish()
        }
    }

    private fun save() {
        binding.saveAccountSettingsBtn.setOnClickListener {
            imageUri?.let { it1 ->
                viewModel.updateProfile(
                    name = binding.accountNameAccountSettings.text.toString(),
                    username = binding.accountUsernameAccountSettings.text.toString(),
                    bio = binding.accountBioAccountSettings.text.toString(),
                    imageUri = it1
                )
            }

            finish()
        }
    }

    private fun changeProfileImage(result: ActivityResultLauncher<CropImageContractOptions>) {
        binding.changeProfileImage.setOnClickListener {
            val options = CropImageOptions(
                allowFlipping = false,
                fixAspectRatio = true,
            )
            result.launch(
                CropImageContractOptions(imageUri, options)
            )
        }
    }

    private fun cropImage() = registerForActivityResult(CropImageContract()) { result ->
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



    private fun setUpUi() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.apply {
                    accountUsernameAccountSettings.setText(uiState.username)
                    accountNameAccountSettings.setText(uiState.fullname)
                    accountBioAccountSettings.setText(uiState.bio)
                    profileImageAccountSettings.load(uiState.image) {
                        placeholder(R.drawable.ic_profile)
                        error(R.drawable.ic_profile)
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }
    }
}