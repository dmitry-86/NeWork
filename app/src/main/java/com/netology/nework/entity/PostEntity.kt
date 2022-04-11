package com.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nework.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var authorId: Long,
    var author: String,
    var authorAvatar: String?,
    var content: String,
    var published: String,
//    var coords: Coordinates? = null,
//    var link: String? = null,
//    var likedByMe: Boolean = false,
//    @Embedded
//    var attachment: AttachmentEmbeddable? = null,
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorAvatar,
        content,
        published,
//        coords,
//        link,
//        likedByMe,
//        attachment?.toDto()
    )


    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
            id = dto.id,
            authorId = dto.authorId,
            author = dto.author,
            authorAvatar = dto.authorAvatar,
            content = dto.content,
            published = dto.published,
//            coords = dto.coords,
//            link = dto.link,
//            likedByMe = dto.likedByMe,
//            attachment = AttachmentEmbeddable.fromDto(dto.attachment),
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)