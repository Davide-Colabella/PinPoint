package com.univpm.pinpointmvvm.model.repo

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.data.User


class SearchRepository {
    private val db = FirebaseDatabase.getInstance().reference.child("users")
    private val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    fun searchUsers(query: String, userList: MutableLiveData<List<User>>) {

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _userList =
                    snapshot.children.map { dataSnapshot -> dataSnapshot.getValue(User::class.java)!! }

                val _userSearched : MutableList<User> = mutableListOf()
                for (user in _userList){
                    if (user.username!!.lowercase().startsWith(query.lowercase()) && user.uid != currentUser.uid){
                        _userSearched.add(user)
                    }
                }
                userList.postValue(_userSearched)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}