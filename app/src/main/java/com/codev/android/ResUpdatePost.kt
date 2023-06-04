package com.codev.android

data class ResUpdatePost(
    val code: Int,
    val result: MessageBodyUpdatePost
)

data class MessageBodyUpdatePost(
    val message: String
)
