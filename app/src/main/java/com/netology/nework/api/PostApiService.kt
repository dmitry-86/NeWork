package com.netology.nework.api

import com.netology.nework.BuildConfig
import com.netology.nework.dto.Post
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostApiService {

        @GET("posts")
        suspend fun getAll(): Response<List<Post>>

        @GET("posts/{id}")
        suspend fun getById(@Path("id") id: Long): Response<Post>

        @POST("posts")
        suspend fun save(@Body post: Post): Response<Post>

        @DELETE("posts/{id}")
        suspend fun removeById(@Path("id") id: Long): Response<Unit>

}

object PostsApi {
    val service: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}

