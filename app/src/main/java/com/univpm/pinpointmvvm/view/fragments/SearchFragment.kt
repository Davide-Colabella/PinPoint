package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentSearchBinding
import com.univpm.pinpointmvvm.view.adapter.SearchAdapter
import com.univpm.pinpointmvvm.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        searchViewListener()
        recyclerViewObserver()
    }

    private fun initUI() {
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchAdapter = SearchAdapter(searchViewModel, requireActivity())

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = searchAdapter
    }

    private fun recyclerViewObserver() {
        searchViewModel.userList.observe(viewLifecycleOwner) {
            searchAdapter.userList = it
        }
    }

    private fun searchViewListener() {
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()){
                    searchViewModel.searchUser(newText)
                }else{
                    searchAdapter.clearList()
                }
                return false
            }
        })
    }
}