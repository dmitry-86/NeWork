package com.netology.nework.model

import com.netology.nework.dto.Job

data class JobFeedModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)