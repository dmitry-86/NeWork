package com.netology.nework.dto

data class User(
    var id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
)