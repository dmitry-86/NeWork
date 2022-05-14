package com.netology.nework.repository

import androidx.lifecycle.LiveData
import com.netology.nework.dto.*
import com.netology.nework.enumeration.AttachmentType
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val data: Flow<List<Event>>
    suspend fun getAll()
    suspend fun save(event: Event)
    suspend fun removeById(id: Long)
    suspend fun saveWithAttachment(event: Event, upload: MediaUpload, type: AttachmentType)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun saveMarker(event: Event, coords: Coordinates)
}