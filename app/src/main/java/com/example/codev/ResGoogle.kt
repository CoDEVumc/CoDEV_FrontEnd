package com.example.codev

data class ResGoogle(
    val code: Int,
    val result: Google
)

data class Google(
    val accessToken: String,
    val key: String,
    val refreshToken: String
)
