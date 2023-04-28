package com.univpm.pinpointmvvm.model.data

import java.io.Serializable

data class User(
    val uid: String? = null,
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = null,
    val bio: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
) : Serializable