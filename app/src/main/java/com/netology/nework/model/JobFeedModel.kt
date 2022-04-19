package com.netology.nework.model

import com.netology.nework.dto.Job

data class JobFeedModel(
//    val posts: List<Post> = emptyList(),
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)