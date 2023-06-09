package com.univpm.pinpointmvvm.model

/**
 * Classe che rappresenta un post
 */
data class Post(
    val imageUrl: String? = null,
    val userId: String? = null,
    var username: String? = null,
    val description: String? = null,
    val date: String? = null,
    val latitude : String? = null,
    val longitude : String? = null,
)