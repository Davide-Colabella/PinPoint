package com.univpm.pinpointmvvm.view.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentCurrentProfileBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.uistate.UserUiState
import com.univpm.pinpointmvvm.view.activities.AccountEditActivity
import com.univpm.pinpointmvvm.view.activities.AccountSettingsActivity
import com.univpm.pinpointmvvm.view.adapter.CurrentUserPostAdapter
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel
import kotlinx.coroutines.launch

class CurrentProfileFragment : Fragment() {
    companion object {
        fun newInstance() = CurrentProfileFragment()
    }

    private val viewModel: CurrentProfileViewModel by viewModels()
    private lateinit var viewBinding: FragmentCurrentProfileBinding
    private lateinit var currentUserPostAdapter : CurrentUserPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserPostAdapter = CurrentUserPostAdapter({
            viewModel.deletePost(it)
        },{
            viewModel.viewOnGoogleMap(it, requireContext())
        })
        viewBinding = FragmentCurrentProfileBinding.inflate(inflater, container, false).apply {
            editProfileButton.setOnClickListener {
                startActivity(Intent(context, AccountEditActivity::class.java))
            }
            profileFragmentSettings.setOnClickListener {
                startActivity(Intent(context, AccountSettingsActivity::class.java))
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                setupUi(uiState)
                observeListOfPosts(uiState)
            }
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDeletePostSuccess(view)
        observeDeletePostError(view)
    }


    private fun observeListOfPosts(uiState: UserUiState) {
        uiState.posts?.observe(viewLifecycleOwner) { posts ->
            for (post in posts) {
                post.username = uiState.username
                post.userPic = uiState.image
            }
            currentUserPostAdapter.posts = posts
            viewBinding.totalPosts.text = posts.size.toString()
            currentUserPostAdapter.notifyDataSetChanged()
        }
    }

    private fun setupUi(uiState: UserUiState) {
        viewBinding.apply {
            profileFragmentUsername.text = uiState.username
            profileFragmentFullName.text = uiState.fullname
            profileFragmentFullName.text = uiState.fullname
            profileFragmentBio.text = uiState.bio
            profileFragmentProfileImage.load(uiState.image) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
                transformations(CircleCropTransformation())
            }
            postRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = currentUserPostAdapter
            }
        }
        uiState.followers?.observe(viewLifecycleOwner) { followers ->
            viewBinding.totalFollowers.text = followers.toString()
        }
        uiState.following?.observe(viewLifecycleOwner) { following ->
            viewBinding.totalFollowing.text = following.toString()
        }
    }

    private fun observeDeletePostError(view: View) {
        lifecycleScope.launch {
            viewModel.postDeleteError.collect {
                if (it.isNotBlank()) {
                    Snackbar.make(view, Constants.POST_SUCCESSFULLY_DELETED, Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.WHITE)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }

        }
    }

    private fun observeDeletePostSuccess(view: View) {
        lifecycleScope.launch {
            viewModel.postDeleteSuccess.collect {
                if (it) {
                    Snackbar.make(view, Constants.POST_UNSUCCESSFULLY_DELETED, Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.WHITE)
                        .setBackgroundTint(Color.GREEN)
                        .show()

                }
            }

        }

    }


}