package com.univpm.pinpointmvvm.uistate

import androidx.lifecycle.LiveData
import com.univpm.pinpointmvvm.model.User

/**
 * Classe che rappresenta lo stato dell'interfaccia grafica della schermata di ricerca
 */
data class SearchUiState(
    val users: LiveData<List<User>>? = null
)