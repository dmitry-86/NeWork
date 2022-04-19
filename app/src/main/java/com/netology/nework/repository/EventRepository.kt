package com.netology.nework.repository

import androidx.lifecycle.LiveData
import com.netology.nework.dto.Event
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val data: Flow<List<Event>>
//    val userData: Flow<List<Event>>
    suspend fun getAll()
    suspend fun save(event: Event)
    suspend fun removeById(id: Long)
//    suspend fun likeById(id: Long)
}