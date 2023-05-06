package com.univpm.pinpointmvvm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.commit451.coiltransformations.SquareCropTransformation
import com.univpm.pinpointmvvm.databinding.ItemPostBinding
import com.univpm.pinpointmvvm.model.data.Post

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    var posts: List<Post> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.postImage.load(post.imageUrl) {
                crossfade(true)
                transformations(SquareCropTransformation())
            }
            binding.postUsername.text = post.username
            binding.postDescription.text = post.description
            binding.postDate.text = post.date.toString()
        }
    }
}


