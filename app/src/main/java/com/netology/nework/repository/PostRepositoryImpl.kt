package com.netology.nework.repository

import android.util.Log
import androidx.lifecycle.Transformations
import com.netology.nework.dao.PostDao
import com.netology.nework.dto.Post
import com.netology.nework.entity.PostEntity

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {
    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            it.toDto()
        }
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    //    override fun likeById(id: Long) {
//        dao.likeById(id)
//    }
}