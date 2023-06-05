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

/**
 * Fragment per la visualizzazione di una pagina per il caricamento di un post
 */
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

        /**
         * Funzione per il caricamento di un post
         * Deve essere garantito l'accesso alla fotocamera e alla localizzazione
         */
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

    /**
     *  Metodo che imposta l'immagine di default a un colore diverso in base al tema
     */
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

    /**
     * Metodo che osserva l'errore di caricamento di un post e mostra un messaggio di errore
     */
    private suspend fun observeUploadPostError() {
        viewModel.postUploadError.collect {
            if (it.isNotBlank()) {
                val error = "$POST_UNSUCCESSFULLY_UPLOADED: \n$it"
                SnackbarManager.onFailure(error, this@PostFragment)
            }
        }

    }

    /**
     * Metodo che osserva il successo del caricamento di un post e mostra un messaggio di successo
     */
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


    /**
     * Metodo che chiude il fragment e torna alla home al termine del caricamento di un post
     */
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

    /**
     * Metodo che permette di caricare un post
     */
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

    /**
     * Metodo che permette di scegliere un'immagine dalla galleria
     */
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
