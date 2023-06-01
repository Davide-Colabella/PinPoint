package com.univpm.pinpointmvvm.view.fragments

import android.app.UiModeManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
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
import com.univpm.pinpointmvvm.utils.Localization
import com.univpm.pinpointmvvm.utils.PermissionsManager
import com.univpm.pinpointmvvm.utils.SnackbarManager
import com.univpm.pinpointmvvm.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    companion object {
        fun newInstance() = PostFragment()
        private const val POST_SUCCESSFULLY_UPLOADED = "Il post è stato caricato"
        private const val POST_UNSUCCESSFULLY_UPLOADED = "Il post non è stato caricato"
        private const val LOCATION_PERMISSION_REQUIRED =
            "Per caricare un post devi abilitare la localizzazione"
        private const val CAMERA_PERMISSION_REQUIRED: String =
            "Per caricare un post devi abilitare la fotocamera"
    }

    //Localization
    private val localization: Localization by lazy { Localization(requireActivity()) }

    //Permissions
    private val permissionsManager: PermissionsManager by lazy { PermissionsManager(requireActivity()) }

    //ViewModel
    private val viewModel: PostViewModel by viewModels()

    //viewBinding
    private lateinit var viewBinding: FragmentPostBinding

    //Image
    private var imageUri = Uri.EMPTY
    private val options = CropImageOptions(
        allowFlipping = false,
        fixAspectRatio = true,
    )
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent!!
            viewBinding.imageviewPost.load(imageUri) {
                crossfade(true)
            }
        } else {
            result.error
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentPostBinding.inflate(inflater, container, false)

        changeDrawableTint()

        lifecycleScope.launch {
            viewModel.setLocalizationEnabled(permissionsManager.checkLocationPermissionForMap())
            viewModel.locationEnabled.collect { locationIsGranted ->
                if (locationIsGranted) {
                    viewModel.setCameraEnabled(permissionsManager.checkCameraPermission())
                    viewModel.cameraEnabled.collect { cameraIsGranted ->
                        if (cameraIsGranted) {
                            onScegliBtnClick()
                            onUploadPost()
                            observeUploadPostSuccess()
                            observeUploadPostError()
                        } else {
                            SnackbarManager.onWarning(CAMERA_PERMISSION_REQUIRED, this@PostFragment)
                        }

                    }

                } else {
                    SnackbarManager.onWarning(LOCATION_PERMISSION_REQUIRED, this@PostFragment)
                }
            }


        }

        return viewBinding.root
    }

    private fun changeDrawableTint() {
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> viewBinding.imageviewPost.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_photo_night))
            AppCompatDelegate.MODE_NIGHT_NO -> viewBinding.imageviewPost.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_photo_light))
            else -> {
                val uiModeManager = requireActivity().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                val isSystemInDarkMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES

                if (isSystemInDarkMode) {
                    viewBinding.imageviewPost.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_photo_night))
                } else {
                    viewBinding.imageviewPost.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_photo_light))
                }
            }
        }
    }

    private suspend fun observeUploadPostError() {
        viewModel.postUploadError.collect {
            if (it.isNotBlank()) {
                val error = "$POST_UNSUCCESSFULLY_UPLOADED: \n$it"
                SnackbarManager.onFailure(error, this@PostFragment)
            }
        }

    }

    private suspend fun observeUploadPostSuccess() {
        viewModel.postUploadSuccess.collect {
            if (it) {
                SnackbarManager.onSuccess(
                    POST_SUCCESSFULLY_UPLOADED,
                    this@PostFragment
                )
                closeFragment()
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
                    val position = localization.getLastLocation()
                    if (position.latitude == Localization.LATITUDE_DEFAULT && position.longitude == Localization.LONGITUDE_DEFAULT) {
                        SnackbarManager.onFailure(
                            "Non è stato possibile ottenere la posizione",
                            this@PostFragment
                        )
                        return@launch
                    }
                    viewModel.uploadPost(
                        imageUri,
                        viewBinding.edittextDescrizione.editText?.text.toString(),
                        position
                    )
                }
            }
        }
    }

    //quando viene premuto il pulsante "sfoglia" si puo scegliere un'immagine dalla galleria
    private fun onScegliBtnClick() {
        viewBinding.btnSfogliaGalleria.setOnClickListener {
            if (imageUri != Uri.EMPTY) {
                viewBinding.imageviewPost.setImageDrawable(null)
                imageUri = Uri.EMPTY
            }
            lifecycleScope.launch {
                if (permissionsManager.checkCameraPermission()) {
                    cropImage.launch(
                        CropImageContractOptions(imageUri, options)
                    )
                }
            }

        }

    }

}
