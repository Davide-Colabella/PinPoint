package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentOtherProfileBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.view.adapter.PostAdapter
import com.univpm.pinpointmvvm.viewmodel.OtherProfileViewModel
import io.getstream.avatarview.coil.loadImage

class OtherProfileFragment : Fragment() {
    private lateinit var binding: FragmentOtherProfileBinding
    private lateinit var user: User
    private var postAdapter = PostAdapter()

    companion object {
        fun newInstance() = OtherProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherProfileBinding.inflate(layoutInflater)
        user = requireArguments().getParcelable(Constants.USER_OBJECT_PARCEL)!!

        val otherProfileViewModelFactory = OtherProfileViewModel.OtherProfileViewModelFactory(user, requireActivity(), postAdapter)
        ViewModelProvider(this,otherProfileViewModelFactory)[OtherProfileViewModel::class.java].apply {
            updateListOfPosts()
        }
        profileUiSetup()

        return binding.root
    }

    private fun profileUiSetup() {
        binding.apply {
            profileActivityProfileImage.loadImage(user.image)
            profileActivityBio.text = user.bio
            profileActivityUsername.text = user.username
            profileActivityFullName.text = user.fullname
            postList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = postAdapter
            }
        }
    }

}