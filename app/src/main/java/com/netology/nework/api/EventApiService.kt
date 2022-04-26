package com.netology.nework.api

import com.netology.nework.BuildConfig
import com.netology.nework.auth.AppAuth
import com.netology.nework.dto.*
import okhttp3.Interceptor
import okhttp3.MultipartBody
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
    .addInterceptor { chain ->
        AppAuth.getInstance().authStateFlow.value.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
        chain.proceed(chain.request())
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface EventApiService {

    @GET("events")
    suspend fun getAll(): Response<List<Event>>

    @GET("events/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Event>

    @POST("events")
    suspend fun save(@Body event: Event): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Event>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

}

object EventsApi {
    val service: EventApiService by lazy {
        retrofit.create(EventApiService::class.java)
    }
}
