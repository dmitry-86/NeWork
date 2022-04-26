package com.netology.nework.entity

import com.netology.nework.enumeration.EventType

data class EventTypeEmbeddable(
    val eventType: String
) {
    fun toDto() = EventType.valueOf(eventType)

    companion object {
        fun fromDto(dto: EventType) = EventTypeEmbeddable(dto.name)
    }

}