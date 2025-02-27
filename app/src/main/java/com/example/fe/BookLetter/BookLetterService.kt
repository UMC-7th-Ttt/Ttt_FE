package com.example.fe.BookLetter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface BookLetterService {
    @GET("/api/book-letters/{bookLetterId}")
    fun getBookLetterDetail(
        @Header("Authorization") token: String, // 헤더에 토큰 추가
        @Path("bookLetterId") bookLetterId: Long // ✅ bookLetterId를 4로 고정
    ): Call<BookLetterResponse>
}