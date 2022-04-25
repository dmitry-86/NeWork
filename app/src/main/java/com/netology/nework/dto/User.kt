package com.netology.nework.dto

data class User(
    var id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
)


//val AnonymousUser = User(
//    id = 0L,
//    login = "anonymous",
//    name = "Anonymous",
//    avatar = "",
//    authorities = listOf("ROLE_ANONYMOUS")
//)