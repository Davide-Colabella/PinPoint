package com.univpm.pinpointmvvm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.SquareCropTransformation
import com.univpm.pinpointmvvm.databinding.ItemPostCurrentUserBinding
import com.univpm.pinpointmvvm.uistate.PostUiState

class CurrentUserPostAdapter(private val deleteListener: (PostUiState) -> Unit, private  val positionListener : (PostUiState) -> Unit) :
    RecyclerView.Adapter<CurrentUserPostAdapter.PostViewHolder>() {

    var posts: List<PostUiState> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            ItemPostCurrentUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: ItemPostCurrentUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostUiState) {
            binding.apply {
                postImage.load(post.imageUrl) {
                    crossfade(true)
                    transformations(SquareCropTransformation())
                }
                postUserPic.load(post.userPic) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                postUsername.text = post.username
                if (post.description == "") {
                    postDescription.isVisible = false
                } else {
                    postDescription.text = post.description
                }

                if (post.latitude.isNullOrBlank() || post.longitude.isNullOrBlank()) {
                    postPosition.text = ""
                } else {
                    postPosition.text = post.latitude + post.longitude
                }

                postDate.text = post.date.toString()
                deletePostBtn.setOnClickListener { deleteListener(post) }
                postPosition.setOnClickListener { positionListener(post) }

            }
        }
    }

}


