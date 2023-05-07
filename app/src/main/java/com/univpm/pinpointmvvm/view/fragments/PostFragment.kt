package com.univpm.pinpointmvvm.view.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentPostBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    companion object {
        fun newInstance() = PostFragment()
    }

    private var imageUri: Uri = Uri.EMPTY
    private val postViewModel: PostViewModel by viewModels()
    private val options = CropImageOptions(
        allowFlipping = false,
        fixAspectRatio = true,
    )
    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onGallery()
        onUploadPost(view)
        observeUploadPostSuccess(view)
        observeUploadPostError(view)
    }

    private fun observeUploadPostError(view: View) {
        lifecycleScope.launch {
            postViewModel.postUploadError.collect {
                if (it.isNotBlank()) {
                    Snackbar.make(view, Constants.POST_UNSUCCESSFULLY_UPLOADED, Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.WHITE)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }

        }
    }

    private fun observeUploadPostSuccess(view: View) {
        lifecycleScope.launch {
            postViewModel.postUploadSuccess.collect {
                if (it) {
                    Snackbar.make(view, Constants.POST_SUCCESSFULLY_UPLOADED, Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.WHITE)
                        .setBackgroundTint(Color.GREEN)
                        .show()
                    closeFragment(requireActivity())

                }
            }

        }

    }

    private fun closeFragment(fragment: FragmentActivity) {
        val destinationFragment = HomeFragment.newInstance()
        fragment.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, destinationFragment)
                .commit()

            findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                selectedItemId = R.id.home
            }
        }
    }

    //Quando viene premuto il pulsante "salva" aggiunge il post sul database
    private fun onUploadPost(view: View) {
        binding.btnSalvaImmagine.setOnClickListener {
            if (binding.imageviewPost.drawable != null) {
                binding.edittextDescrizione.text.toString().apply {
                    postViewModel.uploadPost(imageUri, this)
                }
            }
        }
    }

    //TODO spostare la logica di onGallery in PostViewModel
    //quando viene premuto il pulsante "sfoglia" si puo scegliere un'immagine dalla galleria
    private fun onGallery() {
        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // Use the returned uri.
                imageUri = result.uriContent!!
                binding.imageviewPost.load(imageUri) {
                    crossfade(true)
                    //transformations(SquareCropTransformation())
                }
                Log.d("ImageCropper", imageUri.toString())
            } else {
                // An error occurred.
                val exception = result.error
                Log.d("ImageCropper", exception.toString())
            }
        }
        binding.btnSfogliaGalleria.setOnClickListener {
            cropImage.launch(
                CropImageContractOptions(imageUri, options)
            )
        }
    }
}
