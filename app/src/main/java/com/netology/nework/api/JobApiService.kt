package com.netology.nework.api

import com.netology.nework.BuildConfig
import com.netology.nework.auth.AppAuth
import com.netology.nework.dto.Job
import com.netology.nework.dto.Post
import com.netology.nework.dto.User
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/my/"

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

interface JobApiService {

    @GET("jobs")
    suspend fun getAll(): Response<List<Job>>

    @GET("jobs/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Job>

    @POST("jobs")
    suspend fun save(@Body job: Job): Response<Job>

    @DELETE("jobs/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

//    @FormUrlEncoded
//    @POST("users/authentication")
//    suspend fun authUser(@Field("login") login: String, @Field("pass") pass: String): Response<User>
//
//    @FormUrlEncoded
//    @POST("users/registration")
//    suspend fun registerUser(
//        @Field("login") login: String,
//        @Field("pass") pass: String,
//        @Field("name") name: String
//    ): Response<User>

}

object JobsApi {
    val service: JobApiService by lazy {
        retrofit.create(JobApiService::class.java)
    }
}
