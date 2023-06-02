package com.univpm.pinpointmvvm.repo

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.User
import com.univpm.pinpointmvvm.uistate.PostUiState
import java.util.Locale

class FeedRepository {
    companion object {
        val instance = FeedRepository()
        private const val TAG = "FeedRepositoryDebug"
    }

    private val dbSettings = DatabaseSettings()

    fun getAllPosts(
        allUsers: MutableList<User>,
        onSuccess: (LiveData<List<PostUiState>>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()

        dbSettings.dbPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList: MutableList<PostUiState> = mutableListOf()
                snapshot.children.forEach { child ->
                    child.children.map { obj ->
                        obj.getValue(PostUiState::class.java)
                    }.forEach { postUiState ->
                        if (postUiState != null) {
                            allUsers.find { it.uid == postUiState.userId }?.let { user ->
                                postUiState.username = user.username
                                postUiState.userPic = user.image
                                postList.add(postUiState)
                            }
                        }
                    }
                }

                resultList.value = postList.toList().sortedByDescending { post ->
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd-HH-mm-ss",
                        Locale.getDefault()
                    ).parse(post.date)
                    date.time
                }

                onSuccess(resultList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    fun getFollowers(
        onSuccess: (List<String>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val followers: MutableList<String> = mutableListOf()

        val followersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { followerSnapshot ->
                    val followerId = followerSnapshot.key
                    if (followerId != null) {
                        followers.add(followerId)
                    }
                }
                onSuccess(followers)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }

        dbSettings.dbFollows.child(dbSettings.auth.uid!!).child("followers")
            .addListenerForSingleValueEvent(followersListener)
    }

    fun getFollowing(
        onSuccess: (List<String>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val following: MutableList<String> = mutableListOf()

        val followingListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { followingSnapshot ->
                    val followingId = followingSnapshot.key
                    if (followingId != null) {
                        following.add(followingId)
                    }
                }
                onSuccess(following)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }

        dbSettings.dbFollows.child(dbSettings.auth.uid!!).child("following")
            .addListenerForSingleValueEvent(followingListener)
    }

    fun getCommonUsers(
        onError: (Exception) -> Unit,
        onSuccess: (List<User>) -> Unit
    ) {
        getFollowers({ followers ->
            getFollowing({ following ->
                val commonUserIds = followers.intersect(following)
                val commonUsers: MutableList<User> = mutableListOf()

                if (commonUserIds.isEmpty()) {
                    // Nessun utente comune trovato, chiamare onSuccess immediatamente con una lista vuota
                    onSuccess(commonUsers)
                    return@getFollowing
                }

                var completedCount = 0

                for (userId in commonUserIds) {
                    getUserById(userId, { user ->
                        commonUsers.add(user)
                        completedCount++

                        if (completedCount == commonUserIds.size) {
                            // Tutti gli utenti comuni sono stati recuperati
                            onSuccess(commonUsers)
                        }
                    }, { error ->
                        onError(error)
                    })
                }
            }, { error ->
                onError(error)
            })
        }, { error ->
            onError(error)
        })
    }


    fun getUserById(
        userId: String,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    onSuccess(user)
                } else {
                    onError(Exception("User not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }

        dbSettings.dbUsers.child(userId).addListenerForSingleValueEvent(userListener)
    }

}