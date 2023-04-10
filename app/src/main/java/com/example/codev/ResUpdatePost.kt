package com.example.codev

data class ResUpdatePost(
    val code: Int,
    val result: MessageBodyUpdatePost
)

data class MessageBodyUpdatePost(
    val message: String
)
