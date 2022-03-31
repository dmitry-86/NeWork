package com.netology.nework.repository

import androidx.lifecycle.LiveData
import com.netology.nework.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun save(post: Post)
    fun removeById(id: Long)
    //    fun likeById(id: Long)
}