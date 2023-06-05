package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.FragmentSearchBinding
import com.univpm.pinpointmvvm.uistate.SearchUiState
import com.univpm.pinpointmvvm.view.adapter.SearchAdapter
import com.univpm.pinpointmvvm.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fragment per la visualizzazione della ricerca
 */
class SearchFragment : Fragment() {
    private lateinit var viewBinding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private var searchAdapter = SearchAdapter()

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        searchViewListener()
        observeListOfUserSearched()
        searchUiSetup()
        return viewBinding.root
    }

    /**
     * Metodo per osservare la lista di utenti cercati
     */
    private fun observeListOfUserSearched() {
        lifecycleScope.launch {
            viewModel.uiState.collect { searchUiState ->
                searchUiState.users?.observe(requireActivity()) {
                    searchAdapter.users = it
                    searchAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * Metodo per impostare l'interfaccia utente, in particolare per impostare l'adapter
     * alla lista di utenti cercati
     */

    private fun searchUiSetup() {
        viewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = searchAdapter
        }
    }


    /**
     * Metodo per impostare il listener alla searchView.
     * Qunado il testo cambia, viene chiamato il metodo searchUser
     */
    private fun searchViewListener() {
        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    viewModel.searchUser(newText)
                } else {
                    searchAdapter.clearList()
                }
                return false
            }
        })
    }
}