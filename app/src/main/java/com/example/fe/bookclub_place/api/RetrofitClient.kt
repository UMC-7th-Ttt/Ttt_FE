package com.example.fe.bookclub_place.api

import com.example.fe.scrap.api.ScrapAPI
import com.example.fe.search.api.BookSearchAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://3.38.209.11:8080/"

    // 서버 요청 시 자동으로 Authorization 헤더 추가
    private val authInterceptor = Interceptor { chain ->

        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTc0MDYxNjg2OCwiZW1haWwiOiJhZG1pbjJAbmF2ZXIuY29tIn0.V9bS0xnzzrO9LlBKqHSoHEh0s2whSXMgp9nJNha1UT6S81xAJRsl_GQpz15T_P89Rt9c9-InfWcw-koGcOh1tg"


        val request = chain.request().newBuilder()
            .addHeader("Authorization", token)  // 헤더에 자동으로 토큰 추가
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)  // Interceptor 추가
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val placeApi: PlaceSearchAPI by lazy {
        retrofit.create(PlaceSearchAPI::class.java)
    }

    val bookApi: BookSearchAPI by lazy {
        retrofit.create(BookSearchAPI::class.java)
    }

    val scrapApi: ScrapAPI by lazy {
        retrofit.create(ScrapAPI::class.java)
    }
}
