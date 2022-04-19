package com.netology.nework.adapter

import android.content.Context
import android.os.Build
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
import com.netology.nework.R
import com.netology.nework.databinding.CardEventBinding
import com.netology.nework.dto.Event
import com.netology.nework.dto.Job

interface EventOnInteractionListener {
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    //    fun onLike(post: Post) {}
    //    fun onShare(post: Post) {}
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

    fun bind(event: Event) {
        binding.apply {

            textViewContent.text = event.content
            textViewDate.text = event.datetime.toString()
            eventType.text = event.type.toString()

            delete.setOnClickListener {
                onInteractionListener.onRemove(event)
            }

            edit.setOnClickListener {
                onInteractionListener.onEdit(event)
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