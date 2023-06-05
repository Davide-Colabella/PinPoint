package com.univpm.pinpointmvvm.view.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityAccountEditBinding
import com.univpm.pinpointmvvm.utils.InputValidator
import com.univpm.pinpointmvvm.utils.PermissionsManager
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel
import kotlinx.coroutines.launch

/**
 * Activity per la modifica del profilo utente
 */
class AccountEditActivity : AppCompatActivity() {

    companion object {
        private const val FULLNAME_ERROR = "Inserisci il nome completo. Solo caratteri alfabetici."
        private const val USERNAME_ERROR = "Inserisci l'username. Non immettere degli spazi."
    }

    //Permissions
    private val permissionsManager: PermissionsManager by lazy { PermissionsManager(this) }

    //Input validator
    private val inputValidator = InputValidator()

    //View binding
    private lateinit var binding: ActivityAccountEditBinding

    //ViewModel
    private val viewModel: CurrentProfileViewModel by viewModels()

    //Image cropper
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUi()
        changeProfileImage()
        save()
        discard()
    }

    /**
     * Metodo per la modifica dell'immagine del profilo
     */
    private fun changeProfileImage() {
        binding.changeProfileImage.setOnClickListener {
            if (imageUri != Uri.EMPTY) {
                binding.profileImageAccountSettings.setImageDrawable(null)
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

    /**
     * Metodo per il rifiuto delle modifiche
     */
    private fun discard() {
        binding.closeAccountSettingsBtn.setOnClickListener {
            finish()
        }
    }

    /**
     * Metodo per il salvataggio delle modifiche
     */
    private fun save() {
        binding.saveAccountSettingsBtn.setOnClickListener {
            val fullname = binding.accountNameAccountSettings.editText?.text.toString()
            val username = binding.accountUsernameAccountSettings.editText?.text.toString()
            val bio = binding.accountBioAccountSettings.editText?.text.toString()


            /**
             * Controllo dei dati inseriti dall'utente e se sono validi viene effettuato l'aggiornamento del profilo
             */
            when {
                !inputValidator.isValidFullName(fullname) -> binding.accountNameAccountSettings.error =
                    FULLNAME_ERROR

                !inputValidator.isValidUsername(username) -> binding.accountUsernameAccountSettings.error =
                    USERNAME_ERROR

                else -> {
                    imageUri?.let { uri ->
                        viewModel.updateProfile(
                            name = fullname,
                            username = username,
                            bio = bio,
                            imageUri = uri,
                        )

                        finish()
                    }
                }
            }

        }
    }

    /**
     * Metodo per la visualizzazione dei dati dell'utente
     */
    private fun setUpUi() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.apply {
                    accountUsernameAccountSettings.editText?.setText(uiState.username)
                    accountNameAccountSettings.editText?.setText(uiState.fullname)
                    accountBioAccountSettings.editText?.setText(uiState.bio)
                    profileImageAccountSettings.avatarBorderColor = getColorTheme()
                    profileImageAccountSettings.load(uiState.image) {
                        placeholder(R.drawable.ic_profile)
                        error(R.drawable.ic_profile)
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }
    }

    /**
     * Metodo che restituisce il tema selezionato
     * @return Int
     */
    private fun getColorTheme(): Int {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        return if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            ContextCompat.getColor(this, R.color.primaryNight)
        } else {
            ContextCompat.getColor(this, R.color.primaryLight)
        }
    }
}