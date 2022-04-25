package com.netology.nework.repository

import com.netology.nework.dto.Coordinates
import com.netology.nework.dto.Media
import com.netology.nework.dto.MediaUpload
import com.netology.nework.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<List<Post>>
    val userData: Flow<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun saveMarker(post: Post, coords: Coordinates)
}