package com.example.fe.setting.server

data class ChangeProfileResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val createdAt: String,
        val id: Long,
        val nickname: String,
        val profileUrl: String,
        val role: String
    )
}
