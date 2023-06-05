package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.repo.SearchRepository
import com.univpm.pinpointmvvm.uistate.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione della ricerca
 */
class SearchViewModel : ViewModel() {
    // StateFlow per la gestione dello stato della ricerca
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Repository
    private val searchRepository = SearchRepository()


    /**
     * Metodo per la ricerca di un utente
     * @param query query di ricerca
     */
    fun searchUser(query: CharSequence?) {
        viewModelScope.launch {
            _uiState.update { searchUiState ->
                searchUiState.copy(
                    users = searchRepository.getUserList(query)
                )
            }
        }
    }

}