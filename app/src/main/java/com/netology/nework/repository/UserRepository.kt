package com.netology.nework.repository

import com.netology.nework.dto.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data : Flow<List<User>>
    suspend fun getUserById(id: Long): User
    suspend fun getAll()
}