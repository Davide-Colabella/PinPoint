package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentSearch2Binding
import com.univpm.pinpointmvvm.uistate.SearchUiState
import com.univpm.pinpointmvvm.view.adapter.SearchAdapter
import com.univpm.pinpointmvvm.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var viewBinding: FragmentSearch2Binding
    private var searchAdapter = SearchAdapter()

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSearch2Binding.inflate(inflater, container, false)
        ViewModelProvider(this)[SearchViewModel::class.java].apply {
            searchViewListener(this)
            observeListOfUserSearched(this.uiState)
        }
        searchUiSetup()
        return viewBinding.root
    }

    private fun observeListOfUserSearched(state: StateFlow<SearchUiState>) {
        lifecycleScope.launch {
            state.collect { searchUiState ->
                searchUiState.users?.observe(requireActivity()) {
                    searchAdapter.users = it
                    searchAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    private fun searchUiSetup() {
        viewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = searchAdapter
        }
    }


    private fun searchViewListener(viewModel: SearchViewModel) {

        /*viewBinding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

            }
        })*/

        //viewBinding.searchBar.textView.addTextChangedListener()
    }

}