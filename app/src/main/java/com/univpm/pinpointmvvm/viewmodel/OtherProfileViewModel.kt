package com.univpm.pinpointmvvm.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.univpm.pinpointmvvm.model.data.Post
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.view.adapter.PostAdapter

class OtherProfileViewModel(
    private val user: User,
    private val fragment: FragmentActivity,
    private val postAdapter: PostAdapter
) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    private val posts: LiveData<List<Post>> = _posts
    private val userRepository = UserRepository()

    init{
        userRepository.getPostOfUser(user, _posts)
    }

    fun updateListOfPosts() {
        posts.observe(fragment) {
            for (post in it) {
                post.username = user.username
            }
            postAdapter.posts = it
            postAdapter.notifyDataSetChanged()
        }
    }

    class OtherProfileViewModelFactory(
        private val user: User,
        private val fragment: FragmentActivity,
        private val postAdapter: PostAdapter
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OtherProfileViewModel::class.java)) {
                return OtherProfileViewModel(user, fragment, postAdapter) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}