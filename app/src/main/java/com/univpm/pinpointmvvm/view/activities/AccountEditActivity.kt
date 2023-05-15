package com.univpm.pinpointmvvm.view.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private val viewModel :  CurrentProfileViewModel by viewModels()
    private var imageUri: Uri? = Uri.EMPTY
    private val options = CropImageOptions(
        allowFlipping = false,
        fixAspectRatio = true,
    )
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
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
    private var isPermissionGranted: Boolean = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isPermissionGranted = true
            cropImage.launch(
                CropImageContractOptions(imageUri, options)
            )
        } else {
            //TODO: Handle permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUi()
        changeProfileImage()
        save()
        discard()
    }

    private fun changeProfileImage() {
        binding.changeProfileImage.setOnClickListener {
            if (imageUri != Uri.EMPTY) {
                binding.profileImageAccountSettings.setImageDrawable(null)
                imageUri = Uri.EMPTY
            }
            when {
                isPermissionGranted -> cropImage.launch(
                    CropImageContractOptions(imageUri, options)
                )

                !isPermissionGranted -> {
                    askForPermissions()
                }
            }
        }
    }
    private fun askForPermissions() {
        if (!isPermissionGranted) {
            requestPermissionLauncher.launch(
                android.Manifest.permission.CAMERA
            )
            requestPermissionLauncher.launch(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
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
                    name = binding.accountNameAccountSettings.editText?.text.toString(),
                    username = binding.accountUsernameAccountSettings.editText?.text.toString(),
                    bio = binding.accountBioAccountSettings.editText?.text.toString(),
                    imageUri = it1
                )
            }

            finish()
        }
    }

    private fun setUpUi() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.apply {
                    accountUsernameAccountSettings.editText?.setText(uiState.username)
                    accountNameAccountSettings.editText?.setText(uiState.fullname)
                    accountBioAccountSettings.editText?.setText(uiState.bio)
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