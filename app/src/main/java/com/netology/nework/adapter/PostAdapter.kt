package com.netology.nework.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netology.nework.R
import com.netology.nework.databinding.CardPostBinding
import com.netology.nework.dto.Post
import com.netology.nework.utils.AndroidUtils.formatDate

interface PostOnInteractionListener {
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onLike(post: Post) {}
    fun onLocationClick(post: Post) {}
    fun onImageClick(post: Post) {}
    fun onPlayAudio(post: Post) {}
    fun onPlayVideo(post: Post) {}
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
        with(binding) {
            textViewUserName.text = post.author
            textViewContent.text = post.content
            textViewPublished.text = formatDate(post.published)
//            imageViewAvatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            like.isChecked = post.likedByMe
            like.text = post.likeOwnerIds.count().toString()
            delete.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            edit.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            location.visibility = if (post.coords != null) View.VISIBLE else View.INVISIBLE
            textViewLink.visibility = if (post.link != null) View.VISIBLE else View.INVISIBLE
            attachment.visibility = if (post.attachment != null) View.VISIBLE else View.GONE

            Glide.with(attachment)
                .load("${post.attachment?.url}")
                .timeout(10_000)
                .into(attachment)


            imageViewAvatar.setImageResource(R.drawable.avatar)

            textViewLink.apply {
                this.text = post.link
                this.movementMethod = LinkMovementMethod.getInstance()
                this.setTextColor(Color.BLUE)
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