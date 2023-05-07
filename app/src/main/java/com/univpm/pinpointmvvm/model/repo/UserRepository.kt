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

class UserRepository {
    private val TAG = "UserRepositoryDegub"

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

    fun listenForUserInfoChanges(
        user: User, onUserInfoChanged: (String, String, String, String) -> Unit
    ) {
        DatabaseSettings.dbUsers.child(user.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.getValue(User::class.java)?.apply {
                        onUserInfoChanged(fullname!!, username!!, bio!!, image!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "listenForUserInfoChanges:onCancelled", error.toException())
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

    fun getPostOfUser(user: User): LiveData<List<Post>> {
        var resultList: MutableLiveData<List<Post>> = MutableLiveData()

        DatabaseSettings.dbPosts.child(user.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(Post::class.java)!!
                    }.apply {
                        resultList.value = this.sortedByDescending { it.date }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getPostOfUser:onCancelled", error.toException())
                }
            })


        return resultList
    }

    fun getPostOfUser(): LiveData<List<Post>> {
        var resultList: MutableLiveData<List<Post>> = MutableLiveData()
        DatabaseSettings.dbCurrentUserPosts.apply {
            addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(Post::class.java)!!
                    }.apply {
                        resultList.value = this.sortedByDescending { it.date }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getPostOfUser:onCancelled", error.toException())
                }
            })
        }

        return resultList
    }


    fun deletePost(postToDelete: Post): Task<Void>? {
        var task: Task<Void>? = null
        DatabaseSettings.dbCurrentUserPosts
            .orderByChild("imageUrl").equalTo(postToDelete.imageUrl).apply {
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            if (post?.imageUrl == postToDelete.imageUrl) {
                                // Elimina il post dal database e dallo storage
                                val postId = postSnapshot.key
                                task = postSnapshot.ref.removeValue()
                                //FirebaseStorage.getInstance().getReference("Post/$postId.jpg").delete()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

        return task
    }

}