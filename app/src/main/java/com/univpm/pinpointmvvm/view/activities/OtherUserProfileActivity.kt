package com.univpm.pinpointmvvm.view.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.univpm.pinpointmvvm.databinding.ActivityOtherUserProfileBinding
import com.univpm.pinpointmvvm.model.data.User
import io.getstream.avatarview.coil.loadImage

class OtherUserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtherUserProfileBinding
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras
        val userFromExtras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER_OBJECT", User::class.java)
        } else {
            intent.getParcelableExtra<User>("USER_OBJECT")
        }


        if (userFromExtras != null) {
            binding.profileActivityProfileImage.loadImage(userFromExtras.image)
            binding.profileActivityBio.text = userFromExtras.bio
            binding.profileActivityUsername.text = userFromExtras.username
            binding.profileActivityFullName.text = userFromExtras.fullname
        }
    }
}