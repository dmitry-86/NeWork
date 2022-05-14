package com.netology.nework.repository

import com.netology.nework.dto.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data : Flow<List<User>>
    suspend fun getUserById(id: Long): User
    suspend fun getAll()
    suspend fun save(user: User)
    suspend fun saveWithAttachment(user: User, upload: PhotoUpload)
    suspend fun upload(upload: PhotoUpload): Media
}