package com.univpm.pinpointmvvm.model.repo

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.uistate.PostUiState
import io.reactivex.rxjava3.plugins.RxJavaPlugins.onError
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
        val currentUserId = dbSettings.auth.uid

        dbSettings.dbPosts.addValueEventListener(object : ValueEventListener {
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

                onSuccess(resultList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }


    fun getAllUsers(): MutableList<User> {
        val allUsers: MutableList<User> = mutableListOf()

        dbSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(User::class.java)?.let { user -> allUsers.add(user) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
        return allUsers
    }

}