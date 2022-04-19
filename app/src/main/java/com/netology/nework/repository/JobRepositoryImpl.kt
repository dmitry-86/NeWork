package com.netology.nework.repository

import android.util.Log
import androidx.lifecycle.*
import com.netology.nework.api.*
import com.netology.nework.auth.AppAuth
import com.netology.nework.dao.JobDao
import com.netology.nework.dao.PostDao
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.entity.JobEntity
import com.netology.nework.entity.PostEntity
import com.netology.nework.entity.toDto
import com.netology.nework.entity.toEntity
import com.netology.nework.error.ApiError
import com.netology.nework.error.AppError
import com.netology.nework.error.NetworkError
import com.netology.nework.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException

class JobRepositoryImpl(private val dao: JobDao) : JobRepository {

    private val authorId = AppAuth.getInstance().authStateFlow.value.id

    override val data = dao.getAll()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override val userData = dao.getUserJobsById(id = authorId)
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = JobsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(job: Job) {
        try {
            val response = JobsApi.service.save(job)
            Log.i("tag", response.toString())
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = JobsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override suspend fun likeById(id: Long) {
//        TODO("Not yet implemented")
//    }
}