package com.univpm.pinpointmvvm.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.univpm.pinpointmvvm.databinding.ActivityOtherUserProfileBinding
import com.univpm.pinpointmvvm.model.data.User
import io.getstream.avatarview.coil.loadImage

class OtherUserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtherUserProfileBinding
    private var bundle: Bundle? = null
    private var userFromExtras: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras
        userFromExtras = bundle?.getSerializable("USER_OBJECT") as User?

        if (userFromExtras != null) {
            binding.profileActivityProfileImage.loadImage(userFromExtras!!.image)
            binding.profileActivityBio.text = userFromExtras!!.bio
            binding.profileActivityUsername.text = userFromExtras!!.username
            binding.profileActivityFullName.text = userFromExtras!!.fullname
        }
    }
}