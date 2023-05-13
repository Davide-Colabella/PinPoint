package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentFeedBinding
import com.univpm.pinpointmvvm.view.adapter.FeedAdapter
import com.univpm.pinpointmvvm.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private lateinit var viewBinding: FragmentFeedBinding
    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter: FeedAdapter by lazy {
        FeedAdapter(positionListener = {
            viewModel.viewOnGoogleMap(it, requireContext())
        }, usernameListener = {
            //TODO aggiungere l'intent a far visualizzare il profilo dell'utente cliccato
        })
    }

    companion object {
        fun newInstance() = FeedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    feedAdapter.posts = it
                    feedAdapter.notifyDataSetChanged()
                }
            }
        }

    }

}