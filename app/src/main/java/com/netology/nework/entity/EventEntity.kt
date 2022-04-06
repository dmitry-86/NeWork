package com.netology.nework.entity

//import androidx.room.Embedded
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.netology.nework.dto.Event
//import com.netology.nework.enumeration.EventType
//import java.time.Instant
//
//@Entity
//data class EventEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long,
////    val author: UserEntity,
//    val content: String,
//    /**
//     * Дата и время проведения
//     */
////    val datetime: Instant?,
////    val published: Instant?,
//    /**
//     * Координаты проведения
//     */
////    @Embedded
////    val coords: CoordinatesEmbeddable? = null,
//    /**
//     * Типы события
//     */
//    val type: EventType,
////    @ElementCollection
//    /**
//     * Id'шники залайкавших
//     */
//    val likeOwnerIds: Set<Long> = emptySet(),
//    /**
//     * Id'шники спикеров
//     */
////    @ElementCollection
//    val speakerIds: Set<Long> = emptySet(),
//    /**
//     * Id'шники участников
//     */
//    val participantsIds: MutableSet<Long> = mutableSetOf(),
//    @Embedded
//    val attachment: AttachmentEmbeddable? = null,
//    val link: String? = null,
//) {
//    fun toDto(myId: Long) = Event(
//        id = id,
////        authorId = author.id,
////        author = author.name,
////        authorAvatar = author.avatar,
//        content = content,
////        datetime = datetime,
////        published = published,
////        coords = coords?.toCoordinates(),
//        type = type,
//        likeOwnerIds = likeOwnerIds,
//        likedByMe = likeOwnerIds.contains(myId),
//        speakerIds = speakerIds,
//        participantsIds = participantsIds,
//        participatedByMe = participantsIds.contains(myId),
//        attachment = attachment?.toDto(),
//        link = link,
//    )
//
//    companion object {
//        fun fromDto(dto: Event) = EventEntity(
//            dto.id,
////            UserEntity(dto.authorId),
//            dto.content,
////            dto.datetime,
////            dto.published,
////            dto.coords?.let(CoordinatesEmbeddable::fromCoordinates),
//            dto.type,
//            mutableSetOf(),
//            dto.speakerIds.toMutableSet(),
//            dto.participantsIds.toMutableSet(),
//            AttachmentEmbeddable.fromDto(dto.attachment),
//            dto.link,
//        )
//    }
//}