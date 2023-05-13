package com.univpm.pinpointmvvm.model.repo

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.uistate.PostUiState
import java.util.Locale

class FeedRepository {
    private val TAG = "FeedRepositoryDebug"

    //qui sorge il problema che ad ogni post non è associato il profile_pic e l'username del post
    //allora faccio in modo di ottenere tutti i dati necessari
    fun getAllPosts(allUsers: MutableList<User>): LiveData<List<PostUiState>> {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()

        val currentUser = DatabaseSettings.auth.value?.currentUser
        val currentUserId = currentUser?.uid

        DatabaseSettings.dbPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList: MutableList<PostUiState> = mutableListOf()
                snapshot.children.forEach { child ->
                    child.children.map { obj ->
                        obj.getValue(PostUiState::class.java)
                    }.forEach { postUiState ->
                        if (postUiState != null && postUiState.userId != currentUserId) {
                            postList.add(postUiState)
                        }
                    }
                }

                postList.forEach { postState ->
                    allUsers.forEach { user ->
                        if (postState.userId == user.uid) {
                            postState.username = user.username
                            postState.userPic = user.image
                        }
                    }
                }

                resultList.value = postList.toList().sortedByDescending { post ->
                    val date = SimpleDateFormat(
                        "EEE MMM dd HH:mm:ss zzz yyyy",
                        Locale.ENGLISH
                    ).parse(post.date)
                    date.time
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "getAllPosts:onCancelled", error.toException())
            }
        })

        return resultList
    }



    fun getAllUsers(): MutableList<User> {
        val allUsers: MutableList<User> = mutableListOf()

        DatabaseSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(User::class.java)?.let { it1 -> allUsers.add(it1) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "getAllUsers:onCancelled", error.toException())
            }
        })
        return allUsers
    }

}