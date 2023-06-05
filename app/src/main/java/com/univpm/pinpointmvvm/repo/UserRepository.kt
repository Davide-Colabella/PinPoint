package com.univpm.pinpointmvvm.repo

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.univpm.pinpointmvvm.model.User
import com.univpm.pinpointmvvm.uistate.PostUiState
import io.reactivex.rxjava3.plugins.RxJavaPlugins.onError
import java.util.Locale

/**
 * Classe che rappresenta il repository per la gestione delle operazioni relative agli utenti
 */
class UserRepository {
    companion object {
        private const val TAG = "UserRepositoryDegub"
    }

    private val dbSettings = DatabaseSettings()

    /**
     * Funzione che fetcha tutti gli utenti presenti sul database
     * @param users lista di utenti
     * @see User
     */
    fun fetchAllUsersOnDatabase(users: MutableLiveData<List<User>>) {
        dbSettings.dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedUsers = mutableListOf<User>()
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.key != dbSettings.auth.uid) {
                        val userKey = childSnapshot.key
                        val existingUser = users.value?.find { it.uid == userKey }
                        if (existingUser != null) {
                            existingUser.latitude =
                                childSnapshot.child("latitude").getValue(String::class.java)
                            existingUser.longitude =
                                childSnapshot.child("longitude").getValue(String::class.java)
                            updatedUsers.add(existingUser)
                        } else {
                            val newUser = childSnapshot.getValue(User::class.java)
                            updatedUsers.add(newUser!!)
                        }
                    }
                }
                users.postValue(updatedUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    fun listenForUserInfoChanges(onUserInfoChanged: (String, String, String, String) -> Unit) {

        dbSettings.dbCurrentUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(User::class.java)?.apply {
                    if (fullname != null && username != null && bio != null && image != null) {
                        onUserInfoChanged(fullname, username, bio, image)
                    } else {
                        Log.d(TAG, "onDataChange: user info is null")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }


    fun listenForUserInfoChanges(
        user: User, onUserInfoChanged: (String, String, String, String) -> Unit
    ) {
        dbSettings.dbUsers.child(user.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(User::class.java)?.apply {
                        onUserInfoChanged(fullname!!, username!!, bio!!, image!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())

                }
            })
    }

    /**
     * Funzione che aggiorna la posizione dell'utente
     * @param latitude latitudine
     * @param longitude longitudine
     */
    fun updateProfile(latitude: String, longitude: String) {
        dbSettings.dbCurrentUser.child("latitude").setValue(latitude)
        dbSettings.dbCurrentUser.child("longitude").setValue(longitude)
    }

    /**
     * Funzione che aggiorna il profilo dell'utente
     * @param username username dell'utente
     * @param name nome dell'utente
     * @param bio bio dell'utente
     * @param imageUri immagine dell'utente
     */
    fun updateProfile(username: String, name: String, bio: String, imageUri: Uri = Uri.EMPTY) {
        dbSettings.dbCurrentUser.child("fullname").setValue(name)
        dbSettings.dbCurrentUser.child("username").setValue(username)
        dbSettings.dbCurrentUser.child("bio").setValue(bio)
        setProfileImage(imageUri).addOnSuccessListener { uri ->
            dbSettings.dbCurrentUser.child("image").setValue(uri.toString())
        }
    }


    /**
     * Funzione che aggiorna l'immagine profilo dell'utente
     * @param imageUri immagine dell'utente
     * @return uri dell'immagine
     */
    private fun setProfileImage(imageUri: Uri): Task<Uri> {
        val fileRef = dbSettings.storageProfileImage
            .child(dbSettings.auth.currentUser?.uid + ".jpg")
        val uploadTask = fileRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }
    }

    /**
     * Funzione che esege il logout dell'utente
     */
    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    /**
     * Funzione che restituisce i post dell'utente
     * @param user utente di cui si vogliono i post
     * @return lista di post
     */
    fun getPostOfUser(user: User): LiveData<List<PostUiState>> {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()

        dbSettings.dbPosts.child(user.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.map {
                        it.getValue(PostUiState::class.java)!!
                    }.apply {
                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                        resultList.value = this.sortedByDescending { post ->
                            val date = dateFormat.parse(post.date)
                            date.time
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })


        return resultList
    }

    /**
     * Funzione che elimina un post
     * @param postToDelete post da eliminare
     * @return task di eliminazione
     */
    fun deletePost(postToDelete: PostUiState): Task<Void>? {
        var task: Task<Void>? = null
        dbSettings.dbCurrentUserPosts
            .orderByChild("imageUrl")
            .equalTo(postToDelete.imageUrl)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(PostUiState::class.java)
                        if (post?.imageUrl == postToDelete.imageUrl) {
                            dbSettings.storagePosts.child(postToDelete.userId!!)
                                .child("${postToDelete.date}.jpg").delete()
                            task = postSnapshot.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })


        return task
    }

    /**
     * Funzione che setta il valore del segui a true per l'utente
     * @param user utente da seguire
     */
    fun followUser(user: User) {
        dbSettings.dbFollows
            .child(dbSettings.auth.uid!!)
            .child("following")
            .child(user.uid!!)
            .setValue(true)

        dbSettings.dbFollows
            .child(user.uid)
            .child("followers")
            .child(dbSettings.auth.uid!!)
            .setValue(true)
    }

    /**
     * Funzione che restituisce la lista di post dell'utente
     * @return lista di post
     */
    fun getPostOfUser(): LiveData<List<PostUiState>> {
        val resultList: MutableLiveData<List<PostUiState>> = MutableLiveData()
        dbSettings.dbCurrentUserPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(PostUiState::class.java)!!
                }.apply {
                    val dateFormat =
                        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                    resultList.value = this.sortedByDescending { post ->
                        val date = dateFormat.parse(post.date)
                        date.time
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
        return resultList
    }

    /**
     * Funzione che rimuove il segui ad un utente specifico
     * @param user utente da smettere di seguire
     */
    fun unfollowUser(user: User) {
        dbSettings.dbFollows
            .child(dbSettings.auth.uid!!)
            .child("following")
            .child(user.uid!!)
            .removeValue()

        dbSettings.dbFollows
            .child(user.uid)
            .child("followers")
            .child(dbSettings.auth.uid!!)
            .removeValue()
    }

    /**
     * Funzione che restituisce il numero di utenti che seguono l'utente
     * @param user utente di cui si vuole sapere il numero di utenti che lo seguono
     * @return numero di utenti che seguono l'utente
     */
    fun getFollowersOfUser(user: User): LiveData<Int> {
        val followersLiveData = MutableLiveData<Int>()
        dbSettings.dbFollows
            .child(user.uid!!)
            .child("followers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = snapshot.childrenCount.toInt()
                    followersLiveData.value = followers
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })

        return followersLiveData
    }
    /**
     * Funzione che restituisce il numero di utenti che l'utente segue
     * @param user utente di cui si vuole sapere il numero di utenti che segue
     * @return numero di utenti che l'utente segue
     */
    fun getFollowingOfUser(user: User): LiveData<Int> {
        val followingLiveData = MutableLiveData<Int>()
        dbSettings.dbFollows
            .child(user.uid!!)
            .child("following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = snapshot.childrenCount.toInt()
                    followingLiveData.value = following
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        return followingLiveData
    }

    /**
     * Funzione che restituisce il numero di utenti che seguono l'utente corrente
     * @return numero di utenti che seguono l'utente corrente
     */
    fun getFollowersOfCurrentUser(): LiveData<Int> {
        val followersLiveData = MutableLiveData<Int>()
        dbSettings.dbFollows
            .child(dbSettings.auth.uid!!)
            .child("followers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followers = snapshot.childrenCount.toInt()
                    followersLiveData.value = followers
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        return followersLiveData
    }

    /**
     * Funzione che restituisce il numero di utenti che l'utente corrente segue
     * @return numero di utenti che l'utente corrente segue
     */
    fun getFollowingOfCurrentUser(): LiveData<Int> {
        val followingLiveData = MutableLiveData<Int>()
        dbSettings.dbFollows
            .child(dbSettings.auth.uid!!)
            .child("following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val following = snapshot.childrenCount.toInt()
                    followingLiveData.value = following
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        return followingLiveData
    }


    /**
     * Funzione che restituisce se l'utente corrente segue l'utente passato come parametro
     * @param user utente da controllare
     * @return true se l'utente corrente segue l'utente passato come parametro, false altrimenti
     */
    fun checkFollowing(user: User): LiveData<Boolean> {
        val following = MutableLiveData<Boolean>()
        dbSettings.dbFollows
            .child(dbSettings.auth.uid!!)
            .child("following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    following.value = snapshot.child(user.uid!!).exists()
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
        return following
    }

    /**
     * Funzione che restituisce se i due utenti si seguono a vicenda
     * @param user utente da controllare
     * @return true se i due utenti si seguono a vicenda, false altrimenti
     */
    fun checkBothUsersFollowing(user: User): LiveData<Boolean> {
        val bothFollowing = MutableLiveData<Boolean>()
        val currentUserUid = dbSettings.auth.uid

        if (currentUserUid != null) {
            val currentUserFollowingRef = dbSettings.dbFollows
                .child(currentUserUid)
                .child("following")

            val otherUserFollowingRef = currentUserFollowingRef.child(user.uid!!)

            otherUserFollowingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isCurrentUserFollowingOtherUser = snapshot.exists()

                    // Check if the other user follows the current user
                    val otherUserFollowingCurrentUserRef = dbSettings.dbFollows
                        .child(user.uid)
                        .child("following")
                        .child(currentUserUid)

                    otherUserFollowingCurrentUserRef.addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(innerSnapshot: DataSnapshot) {
                            val isOtherUserFollowingCurrentUser = innerSnapshot.exists()

                            // Check if both users follow each other
                            val areBothFollowing =
                                isCurrentUserFollowingOtherUser && isOtherUserFollowingCurrentUser

                            bothFollowing.value = areBothFollowing
                        }

                        override fun onCancelled(innerError: DatabaseError) {
                            // Handle inner error
                            onError(innerError.toException())
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    onError(error.toException())
                }
            })
        } else {
            // Handle case when current user UID is null
        }

        return bothFollowing
    }

}