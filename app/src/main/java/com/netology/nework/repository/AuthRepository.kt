package com.netology.nework.repository

import com.netology.nework.api.UserApi
import com.netology.nework.dto.PhotoUpload
import com.netology.nework.dto.Token
import com.netology.nework.error.ApiError
import com.netology.nework.error.NetworkError
import com.netology.nework.error.UnknownError
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

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

    suspend fun registerUser(login: String, password: String, name: String): Token {
        try {
            val response = UserApi.service.registerUser(login, password, name)
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

    suspend fun registerUserWithAvatar(login: String, pass: String, name: String, upload: PhotoUpload?): Token {
        try {
            val avatar = MultipartBody.Part.createFormData(
                "file", upload?.file?.name, upload?.file?.asRequestBody()!!
            )
            val response = UserApi.service.registerUserWithAvatar(login, pass, name, avatar)
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