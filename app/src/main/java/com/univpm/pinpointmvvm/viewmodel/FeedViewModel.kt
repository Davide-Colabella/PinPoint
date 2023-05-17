package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.FeedRepository
import com.univpm.pinpointmvvm.uistate.FeedUiState
import com.univpm.pinpointmvvm.uistate.PostUiState
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    private val feedRepository = FeedRepository.instance
    private val allUsers: MutableList<User> = feedRepository.getAllUsers()

    init {
        viewModelScope.launch {
            _uiState.value = FeedUiState.loading()
            feedRepository.getAllPosts(
                allUsers,
                onSuccess = {
                    _uiState.value = FeedUiState.success()
                    _uiState.update { state ->
                        state.copy(posts = it)
                    }
                },
                onError = {
                    _uiState.value = FeedUiState.error(it.toString())
                }
            )
        }
    }

    fun viewOnGoogleMap(postUiState: PostUiState, context: Context) {
        val locationString = "${postUiState.latitude},${postUiState.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$locationString"))
        intent.setPackage("com.google.android.apps.maps")
        ContextCompat.startActivity(context, intent, null)
    }

    fun goToUserClickedProfile(user: User, fragment: FragmentActivity) {
        val bundle = Bundle().apply {
            putParcelable(Constants.USER_OBJECT_PARCEL, user)
        }
        val destinationFragment = OtherProfileFragment.newInstance().apply {
            arguments = bundle
        }

        fragment.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, destinationFragment).commit()
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                selectedItemId = 0
            }
        }
    }
}