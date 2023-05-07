package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentOtherProfileBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.uistate.UserUiState
import com.univpm.pinpointmvvm.view.adapter.CurrentUserPostAdapter
import com.univpm.pinpointmvvm.view.adapter.OtherUserPostAdapter
import com.univpm.pinpointmvvm.viewmodel.OtherProfileViewModel
import io.getstream.avatarview.coil.loadImage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtherProfileFragment : Fragment() {
    private lateinit var viewBinding: FragmentOtherProfileBinding
    private lateinit var user: User
    private var otherUserPostAdapter = OtherUserPostAdapter()

    companion object {
        fun newInstance() = OtherProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewBinding = FragmentOtherProfileBinding.inflate(layoutInflater)
        user = requireArguments().getParcelable(Constants.USER_OBJECT_PARCEL)!!

        val otherProfileViewModelFactory = OtherProfileViewModel.OtherProfileViewModelFactory(user)
        ViewModelProvider(
            this,
            otherProfileViewModelFactory
        )[OtherProfileViewModel::class.java].apply {
            
            observeListOfPosts(this.uiState)
            profileUiSetup(this.uiState)
        }

        return viewBinding.root
    }


    private fun observeListOfPosts(state: StateFlow<UserUiState>) {
        lifecycleScope.launch {
            state.collect{userUiState ->
                userUiState.posts?.observe(requireActivity()) {
                    for (post in it) {
                        post.username = userUiState.username
                    }
                    otherUserPostAdapter.posts = it
                    otherUserPostAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun profileUiSetup(state: StateFlow<UserUiState>) {
        lifecycleScope.launch {
            state.collect {
                viewBinding.apply {
                    profileActivityProfileImage.loadImage(it.image)
                    profileActivityBio.text = it.bio
                    profileActivityUsername.text = it.username
                    profileActivityFullName.text = it.fullname
                    postList.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        setHasFixedSize(true)
                        adapter = otherUserPostAdapter
                    }
                }
            }
        }
    }

}