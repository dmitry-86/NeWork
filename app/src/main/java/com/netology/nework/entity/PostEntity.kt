package com.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.nework.dto.Coordinates
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
    @Embedded
    var coords: CoordinatesEmbeddable? = null,
    var link: String? = null,
    var likedByMe: Boolean = false,
    var likes: Int,
    var ownedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        coords = coords?.toCoordinates(),
        link = link,
        likedByMe = likedByMe,
        likes = likes,
        ownedByMe = ownedByMe,
        attachment = attachment?.toDto()
    )


    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
            dto.id,
            dto.authorId,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
            dto.link,
            dto.likedByMe,
            dto.likes,
            dto.ownedByMe,
            AttachmentEmbeddable.fromDto(dto.attachment),
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)