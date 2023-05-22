package com.univpm.pinpointmvvm.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ItemUserSearchedBinding
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var users: List<User> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemUserSearchedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = users[position]
        holder.itemView.setOnClickListener {
            val fragment = holder.itemView.context as FragmentActivity
            goToUserClickedProfile(fragment, item)
        }
        holder.bind(item)
    }

    private fun goToUserClickedProfile(fragment: FragmentActivity, user: User) {
        val bundle = Bundle().apply {
            putParcelable(Constants.USER_OBJECT_PARCEL, user)
        }
        val destinationFragment = OtherProfileFragment.newInstance().apply {
            arguments = bundle
        }

        fragment.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, destinationFragment).commit()
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                selectedItemId = 0
            }
        }
    }


    fun clearList() {
        users = emptyList()
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(private val binding: ItemUserSearchedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                userProfileImageSearch.load(user.image) {
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                    transformations(CircleCropTransformation())
                }
                fullnameSearch.text = user.fullname
                usernameSearch.text = user.username
            }
        }
    }
}