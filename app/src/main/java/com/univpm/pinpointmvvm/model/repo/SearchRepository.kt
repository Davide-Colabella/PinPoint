package com.univpm.pinpointmvvm.model.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.data.User


class SearchRepository {

private val TAG = "SearchRepositoryDebug"
    fun getUserList(query: String) : MutableLiveData<List<User>> {
        val resultList: MutableLiveData<List<User>> = MutableLiveData()
        var arrayOfUserThatMatch = mutableListOf<User>()

        DatabaseSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(User::class.java)!!
                }.apply {
                    for (user in this) {
                        if (user.username!!.lowercase().startsWith(query.lowercase())
                            && user.uid != DatabaseSettings.currentUserUid
                        ) {
                            arrayOfUserThatMatch.add(user)
                        }
                    }
                }
                resultList.value = arrayOfUserThatMatch
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "getUserList:onCancelled", error.toException())
            }
        })

        return  resultList
    }
}