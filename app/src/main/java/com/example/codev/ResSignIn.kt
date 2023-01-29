package com.example.codev

data class ResSignIn(
    val code: Int,
    val result: Token
)

data class Token(
    val accessToken: String,
    val key: String,
    val refreshToken: String,
    val co_password: String,
    val co_email: String
)
