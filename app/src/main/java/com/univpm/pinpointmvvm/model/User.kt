package com.univpm.pinpointmvvm.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Classe che rappresenta un utente
 */
@Parcelize
data class User(
    val uid: String? = null,
    val fullname: String? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = "https://firebasestorage.googleapis.com/v0/b/pinpointmvvm.appspot.com/o/Default%20Images%2FProfilePicture.png?alt=media&token=780391e3-37ee-4352-8367-f4c08b0f809d",
    val bio: String? = "Hi there! I'm using Pinpoint.",
    var latitude: String? = "41.9027835",
    var longitude: String? = "12.4963655",
) : Parcelable
{
    companion object {
        const val USER_OBJECT_PARCEL = "USER_OBJECT_PARCEL"
    }
}