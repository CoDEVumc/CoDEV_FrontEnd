package com.example.codev

data class ResRefreshToken(
    val code: Int,
    val result: ResponseOfResRefreshToken
)

data class ResponseOfResRefreshToken(
    val accessToken: String
)
