package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ReportFragment
import androidx.lifecycle.ViewModel
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.SearchRepository
import com.univpm.pinpointmvvm.view.activities.UserProfileActivity

class SearchViewModel() : ViewModel() {
    private val repository = SearchRepository()
    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> = _userList


    fun searchUser(query: String) {
        repository.searchUsers(query, _userList)
    }

    fun startShowProfileSearchedActivity(user: User, fragment: FragmentActivity) {
        val intent = Intent(fragment, UserProfileActivity::class.java).apply {
            putExtra(Constants.USER_OBJECT_PARCEL, user)
        }
        fragment.startActivity(intent)
    }

}