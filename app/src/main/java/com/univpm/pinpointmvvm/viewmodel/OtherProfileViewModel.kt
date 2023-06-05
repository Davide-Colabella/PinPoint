package com.univpm.pinpointmvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univpm.pinpointmvvm.model.User
import com.univpm.pinpointmvvm.repo.UserRepository
import com.univpm.pinpointmvvm.uistate.PostUiState
import com.univpm.pinpointmvvm.uistate.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione del profilo utente corrente
 */
class OtherProfileViewModel(user: User) : ViewModel() {
    // StateFlow per la gestione dello stato dell'utente
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    // Repository
    private val userRepository = UserRepository()

    init {
        userRepository.listenForUserInfoChanges(user) { fullname, username, bio, image ->
            viewModelScope.launch {
                _uiState.update { currentState ->
                    currentState.copy(
                        fullname = fullname,
                        username = username,
                        bio = bio,
                        image = image,
                        posts = userRepository.getPostOfUser(user),
                        followers = userRepository.getFollowersOfUser(user),
                        following = userRepository.getFollowingOfUser(user)
                    )
                }
            }
        }
    }

    /**
     * Metodo per visualizzare la posizione del post su Google Maps
     * @param state post
     * @param context contesto
     */
    fun viewOnGoogleMap(state: PostUiState, context: Context) {
        val locationString = "${state.latitude},${state.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$locationString"))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(context, intent, null)
    }

    /**
     * Metodo per seguire un utente
     * @param user utente da seguire
     */
    fun followUser(user: User) {
        userRepository.followUser(user)
    }

    /**
     * Metodo per smettere di seguire un utente
     * @param user utente da smettere di seguire
     */
    fun unfollowUser(user: User) {
        userRepository.unfollowUser(user)
    }

    /**
     * Metodo per controllare se l'utente è seguito
     * @param user utente da controllare
     */
    fun checkFollowing(user: User): LiveData<Boolean> {
        return userRepository.checkFollowing(user)
    }

    /**
     * Metodo per controllare se gli utenti si seguono a vicenda
     * @param user utente da controllare
     */
    fun checkBothUsersFollowing(user: User): LiveData<Boolean> {
        return userRepository.checkBothUsersFollowing(user)
    }

    /**
     * Factory per la creazione del ViewModel
     * @param user utente
     * @return OtherProfileViewModel
     */
    class OtherProfileViewModelFactory(
        private val user: User,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OtherProfileViewModel::class.java)) {
                return OtherProfileViewModel(user) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}