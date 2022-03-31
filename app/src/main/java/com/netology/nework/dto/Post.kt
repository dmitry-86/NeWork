package com.netology.nework.dto

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
//    val authorAvatar: String?,
    val content: String,
//    val published: Instant,
    val published: String,
    val likedByMe: Boolean = false,
//    val attachment: Attachment? = null,
)