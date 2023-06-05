package com.univpm.pinpointmvvm.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Classe che rappresenta le impostazioni del database. Contiene tutte le informazioni necessarie per interagire con il database.
 */
class DatabaseSettings {
    companion object {
        private const val DATABASE_USERS_PATH = "users"
        private const val DATABASE_POSTS_PATH = "posts"
        private const val DATABASE_FOLLOWS_PATH = "follows"
        private const val STORAGE_PROFILE_IMAGE_PATH = "Profile Pictures"
        private const val STORAGE_POSTS_PATH = "Post"
    }

    //FirebaseAuth
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    //FirebaseDatabase
    private val reference = FirebaseDatabase.getInstance().reference
    lateinit var dbCurrentUser: DatabaseReference
    lateinit var dbCurrentUserPosts: DatabaseReference
    val dbUsers = reference.child(DATABASE_USERS_PATH)
    val dbPosts = reference.child(DATABASE_POSTS_PATH)
    val dbFollows = reference.child(DATABASE_FOLLOWS_PATH)
    val storageProfileImage = FirebaseStorage.getInstance().reference.child(
        STORAGE_PROFILE_IMAGE_PATH
    )
    val storagePosts = FirebaseStorage.getInstance().reference.child(STORAGE_POSTS_PATH)

    init {
        if (auth.currentUser != null) {
            dbCurrentUser = auth.uid?.let { reference.child(DATABASE_USERS_PATH).child(it) }!!
            dbCurrentUserPosts = auth.uid?.let { reference.child(DATABASE_POSTS_PATH).child(it) }!!
        }
    }
}