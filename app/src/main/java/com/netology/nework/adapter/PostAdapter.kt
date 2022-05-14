package com.netology.nework.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.netology.nework.BuildConfig
import com.netology.nework.R
import com.netology.nework.databinding.CardPostBinding
import com.netology.nework.dto.Post
import com.netology.nework.enumeration.AttachmentType
import com.netology.nework.utils.AndroidUtils.formatDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface PostOnInteractionListener {
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onLike(post: Post) {}
    fun onLocationClick(post: Post) {}
    fun onImageClick(post: Post) {}
    fun onPlayAudio(post: Post){}
    fun onPlayVideo(post: Post){}
    fun onLinkClick(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: PostOnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
//        holder.itemView.setOnClickListener{
//            onInteractionListener.onItemClick(position)
//        }
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: PostOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun bind(post: Post) {
        binding.apply {
            textViewUserName.text = post.author
            textViewContent.text = post.content
            textViewPublished.text = formatDate(post.published)
            like.isChecked = post.likedByMe
            like.text = post.likeOwnerIds.count().toString()
            delete.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            edit.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            location.visibility = if(post.coords!=null) View.VISIBLE else View.INVISIBLE


            if (attachment != null) {
                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        Glide.with(attachment)
                            .load("${post.attachment?.url}")
                            .into(attachment)
                        attachment.visibility = View.VISIBLE
                    }
                }
            }

            imageViewAvatar.setImageResource(R.drawable.avatar)

            if (post.link != null) {
                textViewLink.text = post.link
                textViewLink.movementMethod = LinkMovementMethod.getInstance()
                textViewLink.setTextColor(Color.BLUE)
                textViewLink.visibility = View.VISIBLE
            }

            textViewLink.setOnClickListener {
                onInteractionListener.onLinkClick(post)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            delete.setOnClickListener {
                onInteractionListener.onRemove(post)
            }

            edit.setOnClickListener {
                onInteractionListener.onEdit(post)
            }

            attachment.setOnClickListener {
                onInteractionListener.onImageClick(post)
            }

            playAudio.setOnClickListener {
                onInteractionListener.onPlayAudio(post)
            }

            playVideo.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

            location.setOnClickListener {
                onInteractionListener.onLocationClick(post)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}