package com.univpm.pinpointmvvm.model.repo

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object DatabaseSettings {

    private const val DATABASE_USERS_PATH = "users"
    private const val DATABASE_POSTS_PATH = "posts"
    private const val DATABASE_FOLLOWS_PATH = "follows"
    private const val STORAGE_PROFILE_IMAGE_PATH = "Profile Pictures"
    private const val STORAGE_POSTS_PATH = "Post"

    val auth: MutableLiveData<FirebaseAuth> = MutableLiveData()
    val dbCurrentUser : MutableLiveData<DatabaseReference> = MutableLiveData()
    val dbCurrentUserPosts : MutableLiveData<DatabaseReference> = MutableLiveData()
    val reference = FirebaseDatabase.getInstance().reference
    val dbUsers = reference.child(DATABASE_USERS_PATH)
    val dbPosts = reference.child(DATABASE_POSTS_PATH)
    val dbFollows = reference.child(DATABASE_FOLLOWS_PATH)
    val storageProfileImage = FirebaseStorage.getInstance().reference.child(STORAGE_PROFILE_IMAGE_PATH)
    val storagePosts = FirebaseStorage.getInstance().reference.child(STORAGE_POSTS_PATH)


    init {
        auth.observeForever {firebaseAuth ->
            if(firebaseAuth != null){
                dbCurrentUser.value = reference.child(DATABASE_USERS_PATH).child(firebaseAuth.currentUser!!.uid)
                dbCurrentUserPosts.value = reference.child(DATABASE_POSTS_PATH).child(firebaseAuth.currentUser!!.uid)
            }
        }
    }
}