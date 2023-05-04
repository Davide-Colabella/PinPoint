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
import com.univpm.pinpointmvvm.databinding.FragmentProfileBinding
import com.univpm.pinpointmvvm.view.activities.AccountSettingsActivity
import com.univpm.pinpointmvvm.view.adapter.PostAdapter
import com.univpm.pinpointmvvm.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            profileViewModel.uiState.collect { uiState ->
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

                profileViewModel.getPostsFromFirebase()
                profileViewModel.posts.observe(viewLifecycleOwner) { posts ->
                    postAdapter.posts = posts
                    postAdapter.notifyDataSetChanged()
                }
            }
        }

    }


}