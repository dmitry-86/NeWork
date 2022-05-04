package com.netology.nework.model

import com.netology.nework.dto.User

data class UserModel(
    val users: List<User> = emptyList(),
    val empty: Boolean = false
)