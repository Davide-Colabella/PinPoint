package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.SearchRepository
import com.univpm.pinpointmvvm.view.activities.OtherUserProfileActivity

class SearchViewModel() : ViewModel() {
    private val repository = SearchRepository()
    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> = _userList


    fun searchUser(query: String) {
        repository.searchUsers(query, _userList)
    }

    fun startShowProfileSearchedActivity(user: User, context: Context) {
        val intent = Intent(context, OtherUserProfileActivity::class.java).apply {
            putExtra("USER_OBJECT", user)
        }
        context.startActivity(intent)
    }

}