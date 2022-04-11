package com.netology.nework.dto

data class User(
    val id: Long,
    val token: String
)

//val AnonymousUser = User(
//    id = 0L,
//    login = "anonymous",
//    name = "Anonymous",
//    avatar = "",
//    authorities = listOf("ROLE_ANONYMOUS")
//)