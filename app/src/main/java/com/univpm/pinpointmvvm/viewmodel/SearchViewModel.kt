package com.univpm.pinpointmvvm.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.SearchRepository
import com.univpm.pinpointmvvm.view.adapter.SearchAdapter

class SearchViewModel(
    private val fragment: FragmentActivity,
    private val adapter: SearchAdapter
) :
    ViewModel() {
    private val _userList = MutableLiveData<List<User>>()
    private val userList: LiveData<List<User>> = _userList
    private val repository = SearchRepository()


    fun searchUser(query: String) {
        repository.searchUsers(query, _userList)
    }

    fun updateListOfUsers() {
        userList.observe(fragment) {
            adapter.users = it
        }
    }


    class SearchViewModelFactory(
        private val fragment: FragmentActivity,
        private val adapter: SearchAdapter
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(fragment, adapter) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}