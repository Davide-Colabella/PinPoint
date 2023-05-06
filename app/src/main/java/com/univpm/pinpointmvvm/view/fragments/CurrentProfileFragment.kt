package com.univpm.pinpointmvvm.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentCurrentProfileBinding
import com.univpm.pinpointmvvm.view.activities.AccountEditActivity
import com.univpm.pinpointmvvm.view.activities.AccountSettingsActivity
import com.univpm.pinpointmvvm.view.adapter.PostAdapter
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel
import kotlinx.coroutines.launch

class CurrentProfileFragment : Fragment() {
    companion object {
        fun newInstance() = CurrentProfileFragment()
    }

    private val currentProfileViewModel: CurrentProfileViewModel by viewModels()
    private lateinit var binding: FragmentCurrentProfileBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentProfileBinding.inflate(inflater, container, false)

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(context, AccountEditActivity::class.java))
        }

        binding.profileFragmentSettings.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            currentProfileViewModel.uiState.collect { uiState ->
                binding.profileFragmentUsername.text = uiState.username
                binding.profileFragmentFullName.text = uiState.fullname
                binding.profileFragmentBio.text = uiState.bio
                binding.profileFragmentProfileImage.load(uiState.image) {
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                    transformations(CircleCropTransformation())
                }

                postAdapter = PostAdapter()
                binding.postList.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = postAdapter
                }

                currentProfileViewModel.getPostsFromFirebase()
                currentProfileViewModel.posts.observe(viewLifecycleOwner) { posts ->
                    for (post in posts) {
                        post.username = uiState.username
                    }
                    postAdapter.posts = posts
                    postAdapter.notifyDataSetChanged()
                }
            }
        }

    }


}