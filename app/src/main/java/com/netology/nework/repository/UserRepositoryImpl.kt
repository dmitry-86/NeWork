package com.netology.nework.repository

import com.netology.nework.api.UserApi
import com.netology.nework.dao.UserDao
import com.netology.nework.dto.*
import com.netology.nework.entity.UserEntity
import com.netology.nework.entity.toDto
import com.netology.nework.entity.toEntity
import com.netology.nework.error.ApiError
import com.netology.nework.error.NetworkError
import com.netology.nework.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    override val data = dao.getAll()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = UserApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getUserById(id: Long) : User{
        try {
            val response = UserApi.service.getUserById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw Exception()
        }catch(e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}