package com.example.codev

data class ResGetEmailCode(
    val code: Int,
    val result: Code,
)

data class Code(
    val success: String
)