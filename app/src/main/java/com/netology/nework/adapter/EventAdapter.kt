package com.netology.nework.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netology.nework.R
import com.netology.nework.databinding.CardEventBinding
import com.netology.nework.dto.Event
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.enumeration.AttachmentType
import com.netology.nework.enumeration.EventType
import com.netology.nework.utils.AndroidUtils.formatDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface EventOnInteractionListener {
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onLike(event: Event) {}
    fun onLocationClick(event: Event) {}
    fun onImageClick(event: Event) {}
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
            eventType.text = when(event.type){
                EventType.ONLINE -> "online"
                else -> "offline"
            }
            textViewPublished.text = formatDate(event.published)
            textViewDate.text = formatDate(event.datetime)
            like.isChecked = event.likedByMe
            like.text = event.likeOwnerIds.count().toString()

//            delete.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE
//            edit.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE

            location.visibility = if(event.coords!=null) View.VISIBLE else View.INVISIBLE

            if (attachment != null) {
                when (event.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        Glide.with(attachment)
                            .load("${event.attachment?.url}")
                            .into(attachment)
                        attachment.visibility = View.VISIBLE
                    }
                }
            }

            imageViewAvatar.setImageResource(R.drawable.avatar)

            if (event.link != null) {
                textViewLink.text = event.link
                textViewLink.movementMethod = LinkMovementMethod.getInstance()
                textViewLink.setTextColor(Color.BLUE)
                textViewLink.visibility = View.VISIBLE
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