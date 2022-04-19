package com.netology.nework.model

import com.netology.nework.dto.Event
import com.netology.nework.dto.Job

data class EventFeedModel(
    val events: List<Event> = emptyList(),
    val empty: Boolean = false,
)