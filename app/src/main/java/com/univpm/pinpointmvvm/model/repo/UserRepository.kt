package com.univpm.pinpointmvvm.model.repo

import android.icu.text.SimpleDateFormat
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
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.uistate.PostUiState
import java.util.Locale

class UserRepository {
    private val TAG = "UserRepositoryDegub"

    fun fetchAllUsersOnDatabase(users: MutableLiveData<List<User>>) {
        DatabaseSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedUsers = mutableListOf<User>()
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.key != DatabaseSettings.auth.value!!.currentUser!!.uid) {
                        val userKey = childSnapshot.key
                        val existingUser = users.value?.find { it.uid == userKey }
                        if (existingUser != null) {
                            existingUser.latitude =
                                childSnapshot.child("latitude").getValue(String::class.java)
                            existingUser.longitude =
                                childSnapshot.child("longitude").getValue(String::class.java)
                            updatedUsers.add(existingUser)
                        } else {
                            val newUser = childSnapshot.getValue(User::class.java)
                            updatedUsers.add(newUser!!)
                        }
                    }
                }
                users.postValue(updatedUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MapRepository", "Error fetching users", error.toException())
            }
        })
    }

    fun listenForUserInfoChanges(onUserInfoChanged: (String, String, String, String) -> Unit) {

        DatabaseSettings.dbCurrentUser.value?.addValueEventListener(object :
            ValueEventListener {
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
        DatabaseSettings.dbCurrentUser.value?.child("latitude")?.setValue(latitude)
        DatabaseSettings.dbCurrentUser.value?.child("longitude")?.setValue(longitude)
    }

    fun updateProfile(username: String, name: String, bio: String, imageUri: Uri = Uri.EMPTY) {
        DatabaseSettings.dbCurrentUser.value?.child("fullname")?.setValue(name)
        DatabaseSettings.dbCurrentUser.value?.child("username")?.setValue(username)
        DatabaseSettings.dbCurrentUser.value?.child("bio")?.setValue(bio)
        setProfileImage(imageUri).addOnSuccessListener { uri ->
            DatabaseSettings.dbCurrentUser.value?.child("image")?.setValue(uri.toString())
        }
    }


    private fun setProfileImage(imageUri: Uri): Task<Uri> {
        val fileRef =
            DatabaseSettings.storageProfileImage.child(DatabaseSettings.auth.value?.currentUser?.uid + ".jpg")
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
        DatabaseSettings.auth.value = null
    }

    fun getPostOfUser(user: User): LiveData<List<PostUiState>> {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()

        DatabaseSettings.dbPosts.child(user.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(PostUiState::class.java)!!
                    }.apply {
                        val dateFormat =
                            SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                        resultList.value = this.sortedByDescending { post ->
                            val date = dateFormat.parse(post.date)
                            date.time
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getPostOfUser:onCancelled", error.toException())
                }
            })


        return resultList
    }

    fun deletePost(postToDelete: PostUiState): Task<Void>? {
        var task: Task<Void>? = null
        DatabaseSettings.dbCurrentUserPosts.value
            ?.orderByChild("imageUrl")?.equalTo(postToDelete.imageUrl)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(PostUiState::class.java)
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


        return task
    }


    fun followUser(user: User) {
        DatabaseSettings.dbFollows.child(DatabaseSettings.auth.value!!.currentUser!!.uid)
            .child("following").child(user.uid!!).setValue(true)
        DatabaseSettings.dbFollows.child(user.uid).child("followers")
            .child(DatabaseSettings.auth.value!!.currentUser!!.uid).setValue(true)
    }

    fun getPostOfUser(): LiveData<List<PostUiState>> {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()
        DatabaseSettings.dbCurrentUserPosts.value?.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(PostUiState::class.java)!!
                }.apply {
                    val dateFormat =
                        SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                    resultList.value = this.sortedByDescending { post ->
                        val date = dateFormat.parse(post.date)
                        date.time
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "getPostOfUser:onCancelled", error.toException())
            }
        })


        return resultList
    }

    fun unfollowUser(user: User) {
        DatabaseSettings.dbFollows.child(DatabaseSettings.auth.value!!.currentUser!!.uid)
            .child("following").child(user.uid!!).removeValue()
        DatabaseSettings.dbFollows.child(user.uid).child("followers")
            .child(DatabaseSettings.auth.value!!.currentUser!!.uid).removeValue()
    }

    fun getFollowersOfUser(user: User): LiveData<Int> {
        val followersLiveData = MutableLiveData<Int>()
        DatabaseSettings.dbFollows.child(user.uid!!).child("followers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = snapshot.childrenCount.toInt()
                    followersLiveData.value = followers
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getFollowersOfUser:onCancelled", error.toException())
                }
            })
        return followersLiveData
    }


    fun getFollowingOfUser(user: User): LiveData<Int> {
        val followingLiveData = MutableLiveData<Int>()
        DatabaseSettings.dbFollows.child(user.uid!!).child("following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = snapshot.childrenCount.toInt()
                    followingLiveData.value = following
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getFollowingOfUser:onCancelled", error.toException())
                }
            })
        return followingLiveData
    }

    fun getFollowersOfCurrentUser(): LiveData<Int> {
        val followersLiveData = MutableLiveData<Int>()
        DatabaseSettings.dbFollows.child(DatabaseSettings.auth.value!!.currentUser!!.uid)
            .child("followers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = snapshot.childrenCount.toInt()
                    followersLiveData.value = followers
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getFollowersOfUser:onCancelled", error.toException())
                }
            })
        return followersLiveData
    }

    fun getFollowingOfCurrentUser(): LiveData<Int> {
        val followingLiveData = MutableLiveData<Int>()
        DatabaseSettings.dbFollows.child(DatabaseSettings.auth.value!!.currentUser!!.uid)
            .child("following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = snapshot.childrenCount.toInt()
                    followingLiveData.value = following
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "getFollowingOfUser:onCancelled", error.toException())
                }
            })
        return followingLiveData
    }

    fun checkFollowing(user: User): LiveData<Boolean> {
        var following = MutableLiveData<Boolean>()
        DatabaseSettings.dbFollows.child(DatabaseSettings.auth.value!!.currentUser!!.uid)
            .child("following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    following.value = snapshot.child(user.uid!!).exists()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "checkFollowing:onCancelled", error.toException())
                }
            })
        return following
    }
}