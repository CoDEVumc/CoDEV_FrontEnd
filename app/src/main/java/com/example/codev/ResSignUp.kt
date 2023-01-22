package com.example.codev

data class ResSignUp(
    val code: Int,
    val result: Token
)

data class Token(
    val accessToken: String,
    val key: String,
    val refreshToken: String
)
