package com.example.codev

data class ResCreateNewPost(
    val code: Int,
    val result: MessageBodyPost
)

data class MessageBodyPost(
    val message: String
)
