package com.netology.nework.model

import com.netology.nework.dto.Job
import com.netology.nework.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
//    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)