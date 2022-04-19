package com.netology.nework.repository

import android.util.Log
import androidx.lifecycle.*
import com.netology.nework.api.*
import com.netology.nework.auth.AppAuth
import com.netology.nework.dao.EventDao
import com.netology.nework.dao.JobDao
import com.netology.nework.dao.PostDao
import com.netology.nework.dto.Event
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.entity.*
import com.netology.nework.error.ApiError
import com.netology.nework.error.AppError
import com.netology.nework.error.NetworkError
import com.netology.nework.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException

class EventRepositoryImpl(private val dao: EventDao) : EventRepository {

    private val authorId = AppAuth.getInstance().authStateFlow.value.id

    override val data = dao.getAll()
        .map(List<EventEntity>::toDto)
        .flowOn(Dispatchers.Default)

//    override val userData = dao.getUserEventsById(id = authorId)
//        .map(List<EventEntity>::toDto)
//        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = EventsApi.service.getAll()
            Log.i("tag getall", response.toString())
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

    override suspend fun save(event: Event) {
        try {
            val response = EventsApi.service.save(event)
            Log.i("tag save", response.toString())
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = EventsApi.service.removeById(id)
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