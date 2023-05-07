package com.univpm.pinpointmvvm.model.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object DatabaseSettings {

    private const val DATABASE_USERS_PATH = "users"
    private const val DATABASE_POSTS_PATH = "posts"
    private const val STORAGE_PROFILE_IMAGE_PATH = "Profile Pictures"
    private const val STORAGE_POSTS_PATH = "Post"

    val currentUser = FirebaseAuth.getInstance().currentUser!!
    val currentUserUid = currentUser.uid
    val reference = FirebaseDatabase.getInstance().reference
    val dbUsers = reference.child(DATABASE_USERS_PATH)
    val dbPosts = reference.child(DATABASE_POSTS_PATH)
    val dbCurrentUser = reference.child(DATABASE_USERS_PATH).child(currentUserUid)
    val dbCurrentUserPosts = reference.child(DATABASE_POSTS_PATH).child(currentUserUid)
    val storageProfileImage = FirebaseStorage.getInstance().reference.child(STORAGE_PROFILE_IMAGE_PATH)
    val storagePosts = FirebaseStorage.getInstance().reference.child(STORAGE_POSTS_PATH)

}