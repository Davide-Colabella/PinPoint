package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.adapter.SearchAdapter
import com.univpm.pinpointmvvm.databinding.FragmentProfileBinding
import com.univpm.pinpointmvvm.databinding.FragmentSearchBinding
import com.univpm.pinpointmvvm.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter

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
        adapter = SearchAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun recyclerViewObserver() {
        searchViewModel.userList.observe(viewLifecycleOwner) {
            adapter.userList = it
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
                    adapter.clearList()
                }
                return false
            }
        })
    }
}
