package com.univpm.pinpointmvvm.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentPostBinding
import com.univpm.pinpointmvvm.model.utils.Localization
import com.univpm.pinpointmvvm.model.utils.SnackbarManager
import com.univpm.pinpointmvvm.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    companion object {
        fun newInstance() = PostFragment()
    }

    private val POST_SUCCESSFULLY_UPLOADED = "Il post è stato caricato"
    private val POST_UNSUCCESSFULLY_UPLOADED = "Il post non è stato caricato"
    private var imageUri: Uri = Uri.EMPTY
    private val postViewModel: PostViewModel by viewModels()
    private val options = CropImageOptions(
        allowFlipping = false,
        fixAspectRatio = true,
    )
    private lateinit var viewBinding: FragmentPostBinding
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            imageUri = result.uriContent!!
            viewBinding.imageviewPost.load(imageUri) {
                crossfade(true)
            }
        } else {
            // An error occurred.
            val exception = result.error
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentPostBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onGallery()
        onUploadPost()
        observeUploadPostSuccess()
        observeUploadPostError()
    }

    private fun observeUploadPostError() {
        lifecycleScope.launch {
            postViewModel.postUploadError.collect {
                if (it.isNotBlank()) {
                    val error = "$POST_UNSUCCESSFULLY_UPLOADED: \n$it"
                    SnackbarManager.onFailure(error, this@PostFragment)
                }
            }
        }
    }

    private fun observeUploadPostSuccess() {
        lifecycleScope.launch {
            postViewModel.postUploadSuccess.collect {
                if (it) {
                    SnackbarManager.onSuccess(
                        POST_SUCCESSFULLY_UPLOADED,
                        this@PostFragment
                    )
                    closeFragment()
                }
            }

        }

    }


    private fun closeFragment() {
        val destinationFragment = HomeFragment.newInstance()
        requireActivity().apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, destinationFragment).commit()

            findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                selectedItemId = R.id.home
            }
        }
    }

    //Quando viene premuto il pulsante "salva" aggiunge il post sul database
    private fun onUploadPost() {
        viewBinding.btnSalvaImmagine.setOnClickListener {
            if (imageUri != Uri.EMPTY) {
                lifecycleScope.launch {
                    postViewModel.uploadPost(
                        imageUri,
                        viewBinding.edittextDescrizione.editText?.text.toString(),
                        Localization(requireActivity()).getLastLocation()
                    )
                }
            }
        }
    }

    //TODO spostare la logica di onGallery in PostViewModel
    //quando viene premuto il pulsante "sfoglia" si puo scegliere un'immagine dalla galleria
    private fun onGallery() {
        viewBinding.btnSfogliaGalleria.setOnClickListener {
            if (imageUri != Uri.EMPTY) {
                viewBinding.imageviewPost.setImageDrawable(null)
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
}
