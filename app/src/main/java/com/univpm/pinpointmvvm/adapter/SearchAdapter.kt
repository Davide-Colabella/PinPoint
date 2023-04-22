package com.univpm.pinpointmvvm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.databinding.UserListviewItemBinding


class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var userList = listOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(private val binding: UserListviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.imageView.load(user.image)
            binding.fullnameTextView.text = user.fullname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserListviewItemBinding.inflate(
                LayoutInflater.from(parent.context) ,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun clearList(){
        this.userList = emptyList()
    }
}
