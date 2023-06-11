package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.User
import com.univpm.pinpointmvvm.repo.FeedRepository
import com.univpm.pinpointmvvm.uistate.FeedUiState
import com.univpm.pinpointmvvm.uistate.PostUiState
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione del feed
 */
class FeedViewModel : ViewModel() {
    // StateFlow per la gestione dello stato del feed
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    // Repository
    private val feedRepository = FeedRepository.instance

    // Lista di tutti gli utenti
    private val allUsers: MutableList<User> = mutableListOf()

    init {
        viewModelScope.launch {
            _uiState.value = FeedUiState.loading()

            feedRepository.getCommonUsers(
                onError = { error ->
                    Log.d("FeedViewModel", "Error: $error")
                    _uiState.value = FeedUiState.error(error.toString())
                },
                onSuccess = { commonUsers ->
                    Log.d("FeedViewModel", "Common users: $commonUsers")
                    allUsers.addAll(commonUsers)
                    fetchAllPosts()
                }
            )
        }
    }

    /**
     * Metodo per aggiornare il feed prendendo tutti i post
     */
    private fun fetchAllPosts() {
        feedRepository.getAllPosts(
            allUsers,
            onSuccess = { postList ->
                _uiState.value = FeedUiState.success()
                _uiState.update { state ->
                    state.copy(posts = postList)
                }
            },
            onError = { error ->
                _uiState.value = FeedUiState.error(error.toString())
            }
        )
    }

    /**
     * Metodo visualizzare la posizione del post su Google Maps
     * @param postUiState post da visualizzare
     * @param context contesto
     */
    fun viewOnGoogleMap(postUiState: PostUiState, context: Context) {
        val locationString = "${postUiState.latitude},${postUiState.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$locationString"))
        intent.setPackage("com.google.android.apps.maps")
        if(intent.resolveActivity(context.packageManager) != null){
            ContextCompat.startActivity(context, intent, null)
        }
    }

    /**
     * Metodo per visualizzare il profilo dell'utente che ha pubblicato il post dopo aver
     * effettuato il click sul suo username
     * @param post post da visualizzare
     * @param fragment fragment da sostituire
     */
    fun goToUserClickedProfile(post: PostUiState, fragment: FragmentActivity) {
        val user = allUsers.find { it.username == post.username }!!

        val bundle = Bundle().apply {
            putParcelable(User.USER_OBJECT_PARCEL, user)
        }
        val destinationFragment = OtherProfileFragment.newInstance().apply {
            arguments = bundle
        }

        fragment.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, destinationFragment)
                .commit()
            findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                .apply {
                    selectedItemId = 0
                }
        }
    }
}