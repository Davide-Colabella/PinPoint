package com.univpm.pinpointmvvm.model.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val uid: String? = null,
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = "https://firebasestorage.googleapis.com/v0/b/pinpointmvvm.appspot.com/o/Default%20Images%2Fprofile_picture.png?alt=media&token=b27db6c2-9178-405e-ac23-a8e1e7fa0e28",
    val bio: String? = "Hi there! I'm using Pinpoint.",
    var latitude: String? = null,
    var longitude: String? = null,
) : Parcelable