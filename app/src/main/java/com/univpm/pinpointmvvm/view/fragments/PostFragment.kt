package com.univpm.pinpointmvvm.view.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentPostBinding
import com.univpm.pinpointmvvm.model.repo.UserRepository

class PostFragment : Fragment() {

    companion object {
        fun newInstance() = PostFragment()
    }

    private lateinit var imageUri : Uri
    private lateinit var userRepository: UserRepository
    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSelectImageButtonListener()
        setPostImageButtonListener()

    }

    private fun setPostImageButtonListener() {
        binding.btnSalvaImmagine.setOnClickListener {
            if(binding.imageView.drawable != null){
                userRepository = UserRepository()
                userRepository.uploadPost(imageUri)
            }
        }
    }

    private fun setSelectImageButtonListener() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    imageUri = uri
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    binding.imageView.load(imageUri) {
                        crossfade(true)
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.btnSfogliaGalleria.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


}

