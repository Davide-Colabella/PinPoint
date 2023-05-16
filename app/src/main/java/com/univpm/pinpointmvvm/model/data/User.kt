package com.univpm.pinpointmvvm.model.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val uid: String? = null,
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = "https://firebasestorage.googleapis.com/v0/b/pinpointmvvm.appspot.com/o/Default%20Images%2FProfilePicture.png?alt=media&token=780391e3-37ee-4352-8367-f4c08b0f809d",
    val bio: String? = "Hi there! I'm using Pinpoint.",
    var latitude: String? = "43.3364943",
    var longitude: String? = "12.9076016",
) : Parcelable