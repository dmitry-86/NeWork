package com.netology.nework.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.netology.nework.R
import com.netology.nework.databinding.CardUserBinding
import com.netology.nework.dto.User

interface UserOnInteractionListener {
    fun onItemClick(user: User) {}
}

class UserAdapter(
    private val onInteractionListener: UserOnInteractionListener,
) : androidx.recyclerview.widget.ListAdapter<User, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
//        holder.itemView.setOnClickListener{
//            onInteractionListener.onItemClick(position)
//        }
        holder.bind(user)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onInteractionListener: UserOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun bind(user: User) {
        binding.apply {
            textViewUserName.text = user.name
            textViewLogin.text  = user.login
            imageViewAvatar.setImageResource(R.drawable.avatar)
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}