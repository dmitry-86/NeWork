package com.netology.nework.dto

import java.time.Instant

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
//    val coords: Coordinates?,
//    val link: String? = null,
//    val likedByMe: Boolean = false,
//    val attachment: Attachment? = null,
)