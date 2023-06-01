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

class SearchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    private val searchRepository = SearchRepository()


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