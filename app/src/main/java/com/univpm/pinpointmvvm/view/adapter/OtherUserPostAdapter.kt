package com.univpm.pinpointmvvm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.SquareCropTransformation
import com.univpm.pinpointmvvm.databinding.ItemPostOtherUserBinding
import com.univpm.pinpointmvvm.model.utils.ImageLoadListener
import com.univpm.pinpointmvvm.uistate.PostUiState

class OtherUserPostAdapter(
    private val listener: (PostUiState) -> Unit,
    private val imageLoadListener: ImageLoadListener
    ) :
    RecyclerView.Adapter<OtherUserPostAdapter.PostViewHolder>() {

    var posts: List<PostUiState> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            ItemPostOtherUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: ItemPostOtherUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostUiState) {
            binding.apply {
                postImage.load(post.imageUrl) {
                    crossfade(true)
                    transformations(SquareCropTransformation())
                    listener(onSuccess = { _, _ ->
                        imageLoadListener.onImageLoaded()
                    })
                }
                postUserPic.load(post.userPic) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    listener(onSuccess = { _, _ ->
                        imageLoadListener.onImageLoaded()
                    })
                }
                postUsername.text = post.username
                if (post.description.isNullOrBlank()) {
                    postDescription.isVisible = false
                } else {
                    postDescription.text = post.description
                }

                if (post.latitude.isNullOrBlank() || post.longitude.isNullOrBlank()) {
                    postPosition.text = ""
                } else {
                    val positionToString = "${post.latitude}, ${post.longitude}"
                    postPosition.text = positionToString
                }

                postDate.text = post.date.toString()
                postPosition.setOnClickListener { listener(post) }
            }
        }
    }

}


