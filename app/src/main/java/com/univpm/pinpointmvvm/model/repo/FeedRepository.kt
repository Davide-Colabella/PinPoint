package com.univpm.pinpointmvvm.model.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.uistate.PostUiState

class FeedRepository {
    private val TAG = "FeedRepositoryDebug"

    //qui sorge il problema che ad ogni post non è associato il profile_pic e l'username del post
    //allora faccio in modo di ottenere tutti i dati necessari
    fun getAllPosts(): LiveData<List<PostUiState>> {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()
        val postList: MutableList<PostUiState> = mutableListOf()
        val usernameForCurrentPost: MutableLiveData<String?> = MutableLiveData()
        val userpicForCurrentPost: MutableLiveData<String?> = MutableLiveData()
        DatabaseSettings.dbPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    Log.d(TAG, child.key!!)
                    DatabaseSettings.dbUsers.child(child.key!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                usernameForCurrentPost.value =
                                    snapshot.child("username").getValue(String::class.java)
                                userpicForCurrentPost.value =
                                    snapshot.child("image").getValue(String::class.java)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    child.children.map { obj ->
                        obj.getValue(PostUiState::class.java)
                    }.onEach { postUiState ->
                        if (postUiState != null) {
                            postUiState.username = usernameForCurrentPost.value
                            postUiState.userPic = userpicForCurrentPost.value
                            postList.add(postUiState)
                        }
                    }
                }
                resultList.value = postList.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "getAllPosts:onCancelled", error.toException())
            }
        })

        return resultList

    }

}