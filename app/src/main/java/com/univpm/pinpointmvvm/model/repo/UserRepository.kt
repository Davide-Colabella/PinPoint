package com.univpm.pinpointmvvm.model.repo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import com.univpm.pinpointmvvm.model.data.Post
import com.univpm.pinpointmvvm.model.data.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository {
    fun fetchAllUsersOnDatabase(): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()
        val _usersList = mutableListOf<User>()

        DatabaseSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnap in snapshot.children) {
                    userSnap.getValue(User::class.java).apply {
                        if (this != null) {
                            if (this.uid != DatabaseSettings.currentUserUid) {
                                _usersList.add(this)
                            }
                        }
                    }

                }
                usersLiveData.postValue(_usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MapRepository", "Error fetching users", error.toException())
            }
        })

        return usersLiveData
    }

    fun listenForUserInfoChanges(onUserInfoChanged: (String, String, String, String) -> Unit) {

        DatabaseSettings.dbCurrentUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.getValue(User::class.java)?.apply {
                    onUserInfoChanged(fullname!!, username!!, bio!!, image!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("POST", "getPostsFromFirebase:onCancelled", error.toException())
            }
        })
    }

    fun updateProfile(latitude: String, longitude: String) {
        DatabaseSettings.dbCurrentUser.child("latitude").setValue(latitude)
        DatabaseSettings.dbCurrentUser.child("longitude").setValue(longitude)
    }

    fun updateProfile(username: String, name: String, bio: String, imageUri: Uri = Uri.EMPTY) {
        DatabaseSettings.dbCurrentUser.child("fullname").setValue(name)
        DatabaseSettings.dbCurrentUser.child("username").setValue(username)
        DatabaseSettings.dbCurrentUser.child("bio").setValue(bio)
        setProfileImage(imageUri).addOnSuccessListener { uri ->
            DatabaseSettings.dbCurrentUser.child("image").setValue(uri.toString())
        }
    }

    fun uploadPost(imageUri: Uri, description: String) {
        pushPostOnDb(imageUri).addOnSuccessListener { uri ->
            val post = Post(
                imageUrl = uri.toString(),
                description = description,
                date = Date().toString(),
                userId = DatabaseSettings.currentUserUid
            )
            DatabaseSettings.dbCurrentUserPosts.push().setValue(post)
        }
    }

    private fun pushPostOnDb(imageUri: Uri): Task<Uri> {
        val currentDateTime = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.ITALIAN)
        val formattedDateTime = formatter.format(currentDateTime)
        val fileRef = DatabaseSettings.storagePosts.child(DatabaseSettings.currentUserUid)
            .child("$formattedDateTime.jpg")
        val uploadTask: UploadTask = fileRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }
    }

    private fun setProfileImage(imageUri: Uri): Task<Uri> {
        val fileRef =
            DatabaseSettings.storageProfileImage.child(DatabaseSettings.currentUserUid + ".jpg")
        val uploadTask: UploadTask = fileRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun getPostOfUser(user: User, _posts: MutableLiveData<List<Post>>) {
        DatabaseSettings.dbPosts.child(user.uid!!).apply {
            addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(Post::class.java)!!
                    }.apply {
                        _posts.value = this
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("POST", "getPostsFromFirebase:onCancelled", error.toException())
                }
            })
        }
    }

    fun getPostOfUser(_posts: MutableLiveData<List<Post>>) {
        DatabaseSettings.dbCurrentUserPosts.apply {
            addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(Post::class.java)!!
                    }.apply {
                        _posts.value = this
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("POST", "getPostsFromFirebase:onCancelled", error.toException())
                }
            })
        }
    }
}