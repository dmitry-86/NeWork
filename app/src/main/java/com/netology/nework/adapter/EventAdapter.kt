package com.netology.nework.adapter

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
import com.netology.nework.databinding.CardEventBinding
import com.netology.nework.dto.Event
import com.netology.nework.enumeration.AttachmentType
import com.netology.nework.enumeration.EventType
import com.netology.nework.utils.AndroidUtils.formatDate

interface EventOnInteractionListener {
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onLike(event: Event) {}
    fun onLocationClick(event: Event) {}
    fun onImageClick(event: Event) {}
    fun onPlayAudio(event: Event) {}
    fun onPlayVideo(event: Event) {}
    fun onLinkClick(event: Event) {}
}

class EventAdapter(
    private val onInteractionListener: EventOnInteractionListener,
) : ListAdapter<Event, EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: EventOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(event: Event) {
        binding.apply {
            textViewUserName.text = event.author
            textViewContent.text = event.content
            eventType.text = when (event.type) {
                EventType.ONLINE -> "online"
                else -> "offline"
            }
            textViewPublished.text = formatDate(event.published)
            textViewDate.text = formatDate(event.datetime)
            like.isChecked = event.likedByMe
            like.text = event.likeOwnerIds.count().toString()

            location.visibility = if (event.coords != null) View.VISIBLE else View.INVISIBLE
            textViewLink.visibility = if (event.link != null) View.VISIBLE else View.INVISIBLE

            Glide.with(imageViewAvatar)
                .load("${event.authorAvatar}")
                .circleCrop()
                .placeholder(R.drawable.avatar)
                .timeout(10_000)
                .into(imageViewAvatar)

            when (event.attachment?.type) {
                AttachmentType.IMAGE -> {
                    Glide.with(attachment)
                        .load("${event.attachment?.url}")
                        .timeout(10_000)
                        .into(attachment)
                    attachment.visibility =
                        if (event.attachment != null) View.VISIBLE else View.GONE
                }
            }

            when (event.attachment?.type) {
                AttachmentType.VIDEO ->
                    playVideo.visibility = if (event.attachment != null) View.VISIBLE else View.GONE
                AttachmentType.AUDIO ->
                    playAudio.visibility = if (event.attachment != null) View.VISIBLE else View.GONE
            }

            textViewLink.apply {
                this.text = event.link
                this.movementMethod = LinkMovementMethod.getInstance()
                this.setTextColor(Color.BLUE)
            }

            textViewLink.setOnClickListener {
                onInteractionListener.onLinkClick(event)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }


            delete.setOnClickListener {
                onInteractionListener.onRemove(event)
            }

            edit.setOnClickListener {
                onInteractionListener.onEdit(event)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }

            attachment.setOnClickListener {
                onInteractionListener.onImageClick(event)
            }

            playAudio.setOnClickListener {
                onInteractionListener.onPlayAudio(event)
            }

            playVideo.setOnClickListener {
                onInteractionListener.onPlayVideo(event)
            }

            location.setOnClickListener {
                onInteractionListener.onLocationClick(event)
            }

        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}