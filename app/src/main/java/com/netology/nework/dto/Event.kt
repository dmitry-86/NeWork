package com.netology.nework.dto

import androidx.navigation.NavType
import com.netology.nework.enumeration.EventType
import java.time.Instant
import javax.xml.xpath.XPathConstants.STRING

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: EventType,
//    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
//    val speakerIds: Set<Long> = emptySet(),
//    val participantsIds: Set<Long> = emptySet(),
//    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
    val likes: Long
)