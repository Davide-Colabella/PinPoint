    package com.univpm.pinpointmvvm.model.repo

    import android.net.Uri
    import android.util.Log
    import androidx.lifecycle.MutableLiveData
    import com.google.android.gms.tasks.Task
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.GenericTypeIndicator
    import com.google.firebase.database.ValueEventListener
    import com.google.firebase.database.ktx.database
    import com.google.firebase.ktx.Firebase
    import com.google.firebase.storage.FirebaseStorage
    import com.google.firebase.storage.UploadTask
    import com.univpm.pinpointmvvm.model.data.Post
    import com.univpm.pinpointmvvm.model.data.User
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale

    class UserRepository {
        private val db = FirebaseDatabase.getInstance().reference
        private val user = FirebaseAuth.getInstance().currentUser!!
        private val usersRef = db.child("users").child(user.uid)
        private val userPostRef = db.child("posts").child(user.uid)
        private val profileImageStorageRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")
        private val postStorageRef = FirebaseStorage.getInstance().reference.child("Posts")

        fun listenForUserInfoChanges(onUserInfoChanged: (String, String, String, String) -> Unit) {

            usersRef.addValueEventListener(object : ValueEventListener {
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

        fun updateProfile(latitude:String, longitude : String){
            usersRef.child("latitude").setValue(latitude)
            usersRef.child("longitude").setValue(longitude)
        }

        fun updateProfile(username: String, name: String, bio: String) {
            usersRef.child("fullname").setValue(name)
            usersRef.child("username").setValue(username)
            usersRef.child("bio").setValue(bio)
        }

        fun updateProfile(username: String, name: String, bio: String, imageUri: Uri) {
            usersRef.child("fullname").setValue(name)
            usersRef.child("username").setValue(username)
            usersRef.child("bio").setValue(bio)
            setProfileImage(imageUri).addOnSuccessListener { uri ->
                usersRef.child("image").setValue(uri.toString())
            }
        }


        fun uploadPost(imageUri: Uri){
            setPostImage(imageUri).addOnSuccessListener{
                uri -> userPostRef.push().child("imageUrl").setValue(uri.toString())
            }
        }

        private fun setPostImage(imageUri: Uri): Task<Uri> {
            val currentDateTime = Date()
            val formatter = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.ITALIAN)
            val formattedDateTime = formatter.format(currentDateTime)
            val fileRef = postStorageRef.child(user.uid).child("$formattedDateTime.jpg")
            val uploadTask: UploadTask = fileRef.putFile(imageUri)
            return uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }
        }

        private fun setProfileImage(imageUri: Uri): Task<Uri> {
            val fileRef = profileImageStorageRef.child(user.uid + ".jpg")
            val uploadTask: UploadTask = fileRef.putFile(imageUri)
            return uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }
        }

        fun logOut() {
            FirebaseAuth.getInstance().signOut()
        }

        fun getPostOfUser(user : User, _posts : MutableLiveData<List<Post>>){
            val database = Firebase.database.reference.child("posts").child(user.uid!!)
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val postList = mutableListOf<Post>()
                    for (postSnapshot in snapshot.children) {
                        val postMap = postSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                        val imageUrl = postMap?.get("imageUrl") as String
                        val post = Post(imageUrl)
                        post.let { postList.add(it) }
                    }
                    _posts.value = postList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("POST", "getPostsFromFirebase:onCancelled", error.toException())
                }
            })
        }

        fun getPostOfUser(_posts : MutableLiveData<List<Post>>){
            val database = Firebase.database.reference.child("posts").child(user.uid)
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val postList = mutableListOf<Post>()
                    for (postSnapshot in snapshot.children) {
                        val postMap = postSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                        val imageUrl = postMap?.get("imageUrl") as String
                        val post = Post(imageUrl)
                        post.let { postList.add(it) }
                    }
                    _posts.value = postList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("POST", "getPostsFromFirebase:onCancelled", error.toException())
                }
            })
        }
    }