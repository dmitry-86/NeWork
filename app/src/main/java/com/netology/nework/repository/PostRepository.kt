package com.netology.nework.repository

import androidx.lifecycle.LiveData
import com.netology.nework.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
//    suspend fun likeById(id: Long)
}