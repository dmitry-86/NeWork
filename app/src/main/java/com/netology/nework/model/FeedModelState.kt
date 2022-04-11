package com.netology.nework.model

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val errorLogin: Boolean = false,
    val errorRegistration: Boolean = false
)