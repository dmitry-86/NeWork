package com.netology.nework.repository

import com.netology.nework.api.PostsApi
import com.netology.nework.api.UserApi
import com.netology.nework.dto.Token
import com.netology.nework.dto.User
import com.netology.nework.error.ApiError
import com.netology.nework.error.NetworkError
import com.netology.nework.error.UnknownError

class AuthRepository {

    suspend fun authUser(login: String, pass: String): Token {
        try {
            val response = UserApi.service.authUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun registerUser(login: String, pass: String, name: String): Token {
        try {
            val response = UserApi.service.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}