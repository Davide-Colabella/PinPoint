package com.univpm.pinpointmvvm.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.univpm.pinpointmvvm.databinding.ActivityOtherUserProfileBinding

class OtherUserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtherUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")

        binding.textViewEmail.text = email
        binding.textViewUsername.text = username
    }
}