    package com.univpm.pinpointmvvm.model.repo

    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener

    class UserRepository {
        private val db = FirebaseDatabase.getInstance().reference
        private val user = FirebaseAuth.getInstance().currentUser!!
        private val usersRef = db.child("users").child(user.uid)

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

        fun updateProfile(username: String, name: String, bio: String) {
            usersRef.child("fullname").setValue(name)
            usersRef.child("username").setValue(username)
            usersRef.child("bio").setValue(bio)
        }

        fun logOut() {
            FirebaseAuth.getInstance().signOut()
        }

    }