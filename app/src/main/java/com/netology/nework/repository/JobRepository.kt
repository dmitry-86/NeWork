package com.netology.nework.repository

import androidx.lifecycle.LiveData
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    val data: Flow<List<Job>>
    val userData: Flow<List<Job>>
    suspend fun getAll()
    suspend fun save(job: Job)
    suspend fun removeById(id: Long)
//    suspend fun likeById(id: Long)
}