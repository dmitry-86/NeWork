package com.netology.nework.repository

import com.netology.nework.dto.Media
import com.netology.nework.dto.MediaUpload
import com.netology.nework.dto.Post
import com.netology.nework.dto.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data : Flow<List<User>>
    suspend fun getUserById(id: Long): User
    suspend fun getAll()
    suspend fun save(user: User)
    suspend fun saveWithAttachment(user: User, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
}