package com.univpm.pinpointmvvm.view.adapter

import android.provider.Settings.Secure.getString
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
import com.univpm.pinpointmvvm.databinding.ItemFeedBinding
import com.univpm.pinpointmvvm.utils.ImageLoadListener
import com.univpm.pinpointmvvm.uistate.PostUiState

class FeedAdapter(
    private val positionListener: (PostUiState) -> Unit,
    private val usernameListener: (PostUiState) -> Unit,
    private val imageLoadListener: ImageLoadListener
) : RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    var posts: List<PostUiState> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    inner class PostViewHolder(private val binding: ItemFeedBinding) :
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
                if (post.description.isNullOrBlank()) {
                    postDescription.isVisible = false
                } else {
                    postDescription.text = post.description
                }

                if (post.latitude.isNullOrBlank() || post.longitude.isNullOrBlank()) {
                    postPosition.text = ""
                } else {
                    postPosition.text = "Mostra posizione"
                }
                val firstThreeNumbers = post.date!!.substring(0, 10)
                postDate.text = firstThreeNumbers
                postPosition.setOnClickListener { positionListener(post) }
                postUsername.setOnClickListener { usernameListener(post) }
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
