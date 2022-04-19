package com.netology.nework.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.netology.nework.databinding.CardJobBinding
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface JobOnInteractionListener {
    fun onEdit(job: Job) {}
    fun onRemove(job: Job) {}
    //    fun onLike(post: Post) {}
    //    fun onShare(post: Post) {}
}

class JobsAdapter(
    private val onInteractionListener: JobOnInteractionListener,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, onInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: JobOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {

            name.text = job.name
            position.text = job.position
            started.text = job.start.toString()
            finished.text = job.finish.toString()
            link.text = job.link

            delete.setOnClickListener {
                onInteractionListener.onRemove(job)
            }

            edit.setOnClickListener {
                onInteractionListener.onEdit(job)
            }

        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}