package com.netology.nework.repository

import com.netology.nework.dto.Job
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    val data: Flow<List<Job>>
    val userData: Flow<List<Job>>
    suspend fun getAll()
    suspend fun save(job: Job)
    suspend fun removeById(id: Long)
}