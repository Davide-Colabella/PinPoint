package com.univpm.pinpointmvvm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.commit451.coiltransformations.SquareCropTransformation
import com.univpm.pinpointmvvm.databinding.ItemPostOtherUserBinding
import com.univpm.pinpointmvvm.model.data.Post

class OtherUserPostAdapter() :
    RecyclerView.Adapter<OtherUserPostAdapter.PostViewHolder>() {

    var posts: List<Post> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostOtherUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: ItemPostOtherUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                postImage.load(post.imageUrl) {
                    crossfade(true)
                    transformations(SquareCropTransformation())
                }
                postUsername.text = post.username
                postDescription.text = post.description
                postDate.text = post.date.toString()
            }
        }
    }

}


