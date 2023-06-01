package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentFeedBinding
import com.univpm.pinpointmvvm.utils.ImageLoadListener
import com.univpm.pinpointmvvm.view.adapter.FeedAdapter
import com.univpm.pinpointmvvm.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

class FeedFragment : Fragment(), ImageLoadListener {
    private lateinit var viewBinding: FragmentFeedBinding
    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter: FeedAdapter by lazy {
        FeedAdapter(positionListener = {
            viewModel.viewOnGoogleMap(it, requireContext())
        }, usernameListener = {
            viewModel.goToUserClickedProfile(it, requireActivity())
        }, imageLoadListener = this
        )
    }
    private var numImagesLoaded = 0

    companion object {
        fun newInstance() = FeedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentFeedBinding.inflate(inflater, container, false)
        setupUi()
        observeListOfPosts()
        return viewBinding.root
    }

    private fun setupUi() {
        viewBinding.feedRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = feedAdapter
        }
    }

    private fun observeListOfPosts() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.posts?.observe(requireActivity()) {
                    Log.d("FeedFragment", "observeListOfPosts: $it")
                    if (it.isEmpty()) {
                        viewBinding.progressBarFeed.visibility = View.GONE
                        viewBinding.noPostsTextView.visibility = View.VISIBLE
                    } else {
                        feedAdapter.posts = it
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onImageLoaded() {
        numImagesLoaded++
        if (numImagesLoaded >= feedAdapter.itemCount) {
            viewBinding.progressBarFeed.visibility = View.GONE
            viewBinding.feedRecyclerview.visibility = View.VISIBLE
        }
    }
}