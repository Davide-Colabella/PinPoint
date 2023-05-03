    package com.univpm.pinpointmvvm.model.repo

    import android.net.Uri
    import android.os.Build
    import androidx.annotation.RequiresApi
    import com.google.android.gms.tasks.Task
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import com.google.firebase.storage.FirebaseStorage
    import com.google.firebase.storage.UploadTask
    import java.text.SimpleDateFormat
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    import java.util.Date
    import java.util.Locale
    import java.util.UUID

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
                uri -> userPostRef.push().setValue(uri.toString())
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
    }