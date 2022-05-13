package com.netology.nework.api

import com.netology.nework.BuildConfig
import com.netology.nework.auth.AppAuth
import com.netology.nework.dto.Token
import com.netology.nework.dto.Media
import com.netology.nework.dto.Post
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

interface PostApiService {

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

//    @FormUrlEncoded
//    @POST("users/authentication")
//    suspend fun authUser(@Field("login") login: String, @Field("pass") pass: String): Response<Token>
//
//    @FormUrlEncoded
//    @POST("users/registration")
//    suspend fun registerUser(
//        @Field("login") login: String,
//        @Field("pass") pass: String,
//        @Field("name") name: String
//    ): Response<Token>

}

object PostsApi {
    val service: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}

