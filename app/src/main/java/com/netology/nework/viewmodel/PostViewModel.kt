package com.netology.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.netology.nework.db.AppDb
import com.netology.nework.dto.Post
import com.netology.nework.repository.PostRepository
import com.netology.nework.repository.PostRepositoryImpl
import com.netology.nework.utils.AndroidUtils
import java.lang.String.format
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.Calendar


private val empty = Post(
    id = 0,
    authorId = 0,
    author = "Dima",
//    authorAvatar = "",
    content = "New Post",
    published = SimpleDateFormat("dd.MM, HH:mm").format(Calendar.getInstance().getTime()),
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
            edited.value = edited.value?.copy(content = text)
        }


    fun removeById(id: Long) = repository.removeById(id)

//    fun likeById(id: Long) = repository.likeById(id)

}