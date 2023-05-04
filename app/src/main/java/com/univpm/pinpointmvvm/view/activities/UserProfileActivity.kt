package com.univpm.pinpointmvvm.view.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.univpm.pinpointmvvm.databinding.ActivityUserProfileBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.view.adapter.PostAdapter
import com.univpm.pinpointmvvm.viewmodel.UserProfileViewModel
import io.getstream.avatarview.coil.loadImage

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var postViewModel: UserProfileViewModel
    private lateinit var postAdapter: PostAdapter
    private var bundle: Bundle? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Constants.USER_OBJECT_PARCEL, User::class.java)
        } else {
            intent.getParcelableExtra(Constants.USER_OBJECT_PARCEL)
        }

        binding.profileActivityProfileImage.loadImage(user!!.image)
        binding.profileActivityBio.text = user!!.bio
        binding.profileActivityUsername.text = user!!.username
        binding.profileActivityFullName.text = user!!.fullname

        postAdapter = PostAdapter()
        binding.postList.apply {
            layoutManager = LinearLayoutManager(this@UserProfileActivity)
            adapter = postAdapter
        }

        postViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        postViewModel.getPostsFromFirebase(user!!)
        postViewModel.posts.observe(this) { posts ->
            postAdapter.posts = posts
            postAdapter.notifyDataSetChanged()
        }


    }

}