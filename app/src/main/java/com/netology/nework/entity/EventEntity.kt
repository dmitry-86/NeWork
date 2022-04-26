package com.netology.nework.entity

import androidx.annotation.InspectableProperty
import androidx.navigation.NavType
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.internal.bind.TypeAdapters.STRING
import com.netology.nework.dto.Event
import com.netology.nework.enumeration.EventType
import javax.xml.xpath.XPathConstants.STRING

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbeddable? = null,
    @Embedded
    val type: EventTypeEmbeddable,
    val likedByMe: Boolean,
////    @ElementCollection
//    val likeOwnerIds: Set<Long> = emptySet(),
////    @ElementCollection
//    val speakerIds: Set<Long> = emptySet(),
//    val participantsIds: MutableSet<Long> = mutableSetOf(),
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val link: String? = null,
    val likes: Long
) {
    fun toDto() = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        datetime = datetime,
        published = published,
        coords = coords?.toCoordinates(),
        type = type.toDto(),
//        likeOwnerIds = likeOwnerIds,
        likedByMe = likedByMe,
//        speakerIds = speakerIds,
//        participantsIds = participantsIds,
//        participatedByMe = participantsIds.contains(myId),
        attachment = attachment?.toDto(),
        link = link,
        likes = likes
    )

    companion object {
        fun fromDto(dto: Event) = EventEntity(
            dto.id,
            dto.authorId,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.datetime,
            dto.published,
            dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
            EventTypeEmbeddable.fromDto(dto.type),
            dto.likedByMe,
//            mutableSetOf(),
//            dto.speakerIds.toMutableSet(),
//            dto.participantsIds.toMutableSet(),
            AttachmentEmbeddable.fromDto(dto.attachment),
            dto.link,
            dto.likes
        )
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)