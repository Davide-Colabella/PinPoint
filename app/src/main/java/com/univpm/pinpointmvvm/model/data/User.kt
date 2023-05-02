package com.univpm.pinpointmvvm.model.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val uid: String? = null,
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = null,
    val bio: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
) : Parcelable