package com.codev.android

data class ResGetEmailCode(
    val code: Int,
    val result: Code,
)

data class Code(
    val success: String
)