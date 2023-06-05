package com.univpm.pinpointmvvm.uistate

import com.google.android.gms.maps.model.LatLng

/**
 * Classe che rappresenta lo stato dell'interfaccia grafica della schermata dei post
 */
data class PostUiState(
    val userId : String? = null,
    val imageUrl: String? = null,
    var userPic: String? = null,
    var username: String? = null,
    val description: String? = null,
    val date: String? = null,
    val latitude : String? = null,
    val longitude : String? = null,
)