package com.netology.nework.repository

import com.netology.nework.dto.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data : Flow<List<User>>
    suspend fun getAll()
}