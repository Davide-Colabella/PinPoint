package com.univpm.pinpointmvvm.model.repo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository {
    private val db = FirebaseDatabase.getInstance().reference

    fun listenForUserInfoChanges(onUserInfoChanged: (String, String, String, String) -> Unit) {
        db.child("users").child("3cfTJR9jBtaPLELZlBKV4Z2yqO72").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fullname = dataSnapshot.child("fullname").getValue(String::class.java)
                val username = dataSnapshot.child("username").getValue(String::class.java)
                val bio = dataSnapshot.child("bio").getValue(String::class.java)
                val image = dataSnapshot.child("image").getValue(String::class.java)
                if (fullname != null && username != null && bio != null && image != null) {
                    onUserInfoChanged(fullname, username, bio, image)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur here
            }
        })
    }
}