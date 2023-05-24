package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentOtherProfileBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.utils.ImageLoadListener
import com.univpm.pinpointmvvm.view.adapter.OtherUserPostAdapter
import com.univpm.pinpointmvvm.viewmodel.OtherProfileViewModel
import io.getstream.avatarview.coil.loadImage
import kotlinx.coroutines.launch

class OtherProfileFragment : Fragment(), ImageLoadListener {
    private lateinit var user: User
    private lateinit var viewBinding: FragmentOtherProfileBinding
    private val viewModel: OtherProfileViewModel by viewModels {
        OtherProfileViewModel.OtherProfileViewModelFactory(
            user
        )
    }
    private val otherUserPostAdapter: OtherUserPostAdapter by lazy {
        OtherUserPostAdapter(
            listener = {
                viewModel.viewOnGoogleMap(it, requireContext())
            }, imageLoadListener = this
        )
    }
    private var numImagesLoaded = 0

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

        checkFollowing()
        checkBothUsersFollowing()
        followButtonListener()
        profileUiSetup()

        return viewBinding.root
    }

    private fun getColorTheme(): Int {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        return if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            ContextCompat.getColor(requireContext(), R.color.primaryNight)
        } else {
            ContextCompat.getColor(requireContext(), R.color.primaryLight)
        }
    }

    private fun followButtonListener() {
        viewBinding.followButton.setOnClickListener {
            if (viewBinding.followButton.text.toString() == "Segui")
                viewModel.followUser(user)
            else if (viewBinding.followButton.text.toString() == "Segui già")
                viewModel.unfollowUser(user)
        }
    }

    private fun checkFollowing() {
        viewModel.checkFollowing(user).observe(
            requireActivity()
        ) { isFollowing ->
            if (isFollowing) {
                viewBinding.followButton.text = "Segui già"
            } else {
                viewBinding.followButton.text = "Segui"
            }
        }
    }

    private fun checkBothUsersFollowing() {
        viewModel.checkBothUsersFollowing(user).observe(
            viewLifecycleOwner
        ) { areBothFollowing ->
            if (areBothFollowing) {
                viewBinding.apply {
                    progressBarOtherProfile.visibility = View.GONE
                    appBarLayoutProfile.visibility = View.VISIBLE
                    nestedScrollViewProfile.visibility = View.VISIBLE
                    postList.visibility = View.VISIBLE
                    notFriendsTextView.visibility = View.GONE
                }
                observeListOfPosts()
            } else {
                viewBinding.apply {
                    progressBarOtherProfile.visibility = View.GONE
                    appBarLayoutProfile.visibility = View.VISIBLE
                    nestedScrollViewProfile.visibility = View.VISIBLE
                    postList.visibility = View.GONE
                    noPostsTextView.visibility = View.GONE
                    notFriendsTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeListOfPosts() {
        lifecycleScope.launch {
            viewModel.uiState.collect { userUiState ->
                userUiState.posts?.observe(viewLifecycleOwner) {
                    if (it.isEmpty()) {
                        viewBinding.progressBarOtherProfile.visibility = View.GONE
                        viewBinding.appBarLayoutProfile.visibility = View.VISIBLE
                        viewBinding.nestedScrollViewProfile.visibility = View.VISIBLE
                        viewBinding.noPostsTextView.visibility = View.VISIBLE
                        viewBinding.postList.visibility = View.GONE
                    } else {
                        for (post in it) {
                            post.username = userUiState.username
                            post.userPic = userUiState.image
                        }
                        otherUserPostAdapter.posts = it
                        viewBinding.apply {
                            totalPosts.text = it.size.toString()
                            noPostsTextView.visibility = View.GONE
                        }
                        otherUserPostAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    private fun profileUiSetup() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                viewBinding.apply {
                    profileActivityProfileImage.loadImage(it.image)
                    profileActivityProfileImage.avatarBorderColor = getColorTheme()
                    profileActivityBio.text = it.bio
                    profileActivityUsername.text = it.username
                    profileActivityFullName.text = it.fullname
                    postList.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        setHasFixedSize(true)
                        adapter = otherUserPostAdapter
                    }
                }
                it.followers?.observe(requireActivity()) { followers ->
                    viewBinding.totalFollowers.text = followers.toString()
                }
                it.following?.observe(requireActivity()) { following ->
                    viewBinding.totalFollowing.text = following.toString()
                }
            }
        }
    }

    override fun onImageLoaded() {
        numImagesLoaded++
        if (numImagesLoaded >= otherUserPostAdapter.itemCount) {
            viewBinding.progressBarOtherProfile.visibility = View.GONE
            viewBinding.appBarLayoutProfile.visibility = View.VISIBLE
            viewBinding.nestedScrollViewProfile.visibility = View.VISIBLE
        }
    }
}