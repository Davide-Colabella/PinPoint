package com.univpm.pinpointmvvm.model.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.data.User


class SearchRepository {

   companion object {
       private const val TAG = "SearchRepositoryDebug"
   }
    private val dbSettings = DatabaseSettings()
    fun getUserList(query: CharSequence?) : MutableLiveData<List<User>> {
        val resultList: MutableLiveData<List<User>> = MutableLiveData()
        val arrayOfUserThatMatch = mutableListOf<User>()

        dbSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(User::class.java)!!
                }.apply {
                    for (user in this) {
                        if (user.username!!.lowercase().startsWith(query.toString().lowercase())
                            && user.uid != dbSettings.auth.uid
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