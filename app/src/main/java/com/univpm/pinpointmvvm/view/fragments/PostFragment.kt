package com.univpm.pinpointmvvm.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentPostBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.viewmodel.PostViewModel

class PostFragment : Fragment() {

    companion object {
        fun newInstance() = PostFragment()
    }

    private lateinit var imageUri: Uri
    private val postViewModel: PostViewModel by viewModels()
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

    }

    //Quando viene premuto il pulsante "salva" aggiunge il post sul database
    private fun onUploadPost(view: View) {
        binding.btnSalvaImmagine.setOnClickListener {
            if (binding.imageView.drawable != null) {
                postViewModel.uploadPost(imageUri)

                Snackbar.make(view, Constants.POST_SUCCESSFULLY_UPLOADED, Snackbar.LENGTH_SHORT).show()

                //chiudo il fragment appena carico la foto sul database
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, HomeFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    //quando viene premuto il pulsante "sfoglia" si puo scegliere un'immagine dalla galleria
    private fun onGallery() {

        val req = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.imageView.load(imageUri) {
                    crossfade(true)
                }
            }
        }
        binding.btnSfogliaGalleria.setOnClickListener {
            req.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


}
