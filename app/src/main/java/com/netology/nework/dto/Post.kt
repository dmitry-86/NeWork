package com.netology.nework.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
//    val coords: Coordinates?,
//    val link: String? = null,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val ownedByMe: Boolean = false,
//    val attachment: Attachment? = null,
)