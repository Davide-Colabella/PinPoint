package com.univpm.pinpointmvvm.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.databinding.UserListviewItemBinding
import com.univpm.pinpointmvvm.viewmodel.SearchViewModel


class SearchAdapter(private val searchViewModel: SearchViewModel, private val context: Context) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var userList = listOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(private val binding: UserListviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userProfileImageSearch.load(user.image) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
                transformations(CircleCropTransformation())
            }
            binding.fullnameSearch.text = user.fullname
            binding.usernameSearch.text = user.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserListviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        holder.itemView.setOnClickListener {
            searchViewModel.startShowProfileSearchedActivity(item,context)
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int = userList.size

    fun clearList() {
        this.userList = emptyList()
    }
}