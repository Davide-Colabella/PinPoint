package com.univpm.pinpointmvvm.model.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.univpm.pinpointmvvm.model.data.User

class MapRepository {
    private val database = FirebaseDatabase.getInstance().reference.child("users")

    fun getAllUsers(): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<User>()
                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    if (user != null) {
                        if(user.uid!! != FirebaseAuth.getInstance().currentUser!!.uid) {
                            usersList.add(user)
                        }
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