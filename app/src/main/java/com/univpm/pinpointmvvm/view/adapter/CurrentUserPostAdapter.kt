package com.univpm.pinpointmvvm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.SquareCropTransformation
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ItemPostCurrentUserBinding
import com.univpm.pinpointmvvm.model.utils.ImageLoadListener
import com.univpm.pinpointmvvm.uistate.PostUiState


class CurrentUserPostAdapter(
    private val deleteListener: (PostUiState) -> Unit,
    private val positionListener: (PostUiState) -> Unit,
    private val imageLoadListener: ImageLoadListener
) : RecyclerView.Adapter<CurrentUserPostAdapter.PostViewHolder>() {

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
                    listener(onSuccess = { _, _ ->
                        imageLoadListener.onImageLoaded()
                    })
                }
                postUserPic.avatarBorderColor = getColorTheme()
                postUserPic.load(post.userPic) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    listener(onSuccess = { _, _ ->
                        imageLoadListener.onImageLoaded()
                    })
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
                    val positionToString = "${post.latitude}, ${post.longitude}"
                    postPosition.text = positionToString
                }

                postDate.text = post.date.toString()
                deletePostBtn.setOnClickListener { deleteListener(post) }
                postPosition.setOnClickListener { positionListener(post) }

            }
        }

        private fun getColorTheme(): Int {
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            return if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                ContextCompat.getColor(binding.root.context, R.color.primaryNight)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.primaryLight)
            }
        }
    }

}


