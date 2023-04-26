package com.univpm.pinpointmvvm.model.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.data.User

class MapRepository {
    private val database = FirebaseDatabase.getInstance().reference.child("users")
/*
    fun getUsersFromDatabase(onDataLoaded: (List<User>) -> Unit, onError: (DatabaseError) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (childSnapshot in dataSnapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                onDataLoaded(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }

 */

    fun getAllUsers(): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<User>()
                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    if (user != null) {
                        usersList.add(user)
                    }
                }
                usersLiveData.postValue(usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MapRepository", "Error fetching users", error.toException())
            }
        })

        return usersLiveData
    }

}