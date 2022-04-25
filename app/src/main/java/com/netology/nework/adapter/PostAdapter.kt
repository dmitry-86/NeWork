package com.netology.nework.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netology.nework.BuildConfig
import com.netology.nework.R
import com.netology.nework.databinding.CardPostBinding
import com.netology.nework.dto.Post
import com.netology.nework.enumeration.AttachmentType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface PostOnInteractionListener {
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onLike(post: Post) {}
    fun onItemClick(position: Int) {}
    //    fun onShare(post: Post) {}
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
        holder.itemView.setOnClickListener{
            onInteractionListener.onItemClick(position)
        }
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
            textViewPublished.text = post.published
            textViewContent.text = post.content

            val parsedDate = LocalDateTime.parse(post.published, DateTimeFormatter.ISO_DATE_TIME)
            val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            textViewPublished.text = formattedDate

            like.isChecked = post.likedByMe
            like.text = "${post.likes}"

            delete.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            edit.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            if (attachment != null) {
                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        Glide.with(attachment)
                            .load("${BuildConfig.BASE_URL}/media/${post.attachment?.url}")
                            .into(attachment)
                        attachment.visibility = View.VISIBLE
                    }
                }
            }

            imageViewAvatar.setImageResource(R.drawable.avatar)


            if(post.link != null) {
                textViewLink.settings.javaScriptEnabled = true
                textViewLink.webViewClient
                textViewLink.loadUrl(post.link.toString())
                textViewLink.loadData("<html><body><a>${post.link.toString()}</a></body></html>",
                    "text/html", "UTF-8");
                textViewLink.visibility = View.VISIBLE
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

            location.setOnClickListener {
                onInteractionListener.onItemClick(post.id.toInt())
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