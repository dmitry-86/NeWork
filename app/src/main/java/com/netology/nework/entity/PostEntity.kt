package com.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nework.dto.Post
import java.time.Instant
import java.util.*

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
//    val authorAvatar: String?,
    val content: String,
    val published: String,
//    val published: Instant,
    val likedByMe: Boolean,
) {
    fun toDto() = Post(id,
        authorId,
        author,
//        authorAvatar,
        content,
        published,
        likedByMe)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id,
                dto.authorId,
                dto.author,
//                dto.authorAvatar!!,
                dto.content,
                dto.published,
                dto.likedByMe)
    }
}