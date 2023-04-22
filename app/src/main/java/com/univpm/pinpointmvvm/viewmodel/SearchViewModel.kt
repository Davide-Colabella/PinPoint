package com.univpm.pinpointmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.SearchRepository

class SearchViewModel() : ViewModel() {
    private val repository = SearchRepository()
    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> = _userList


    fun searchUser(query: String) {
        repository.searchUsers(query, _userList)
    }




}