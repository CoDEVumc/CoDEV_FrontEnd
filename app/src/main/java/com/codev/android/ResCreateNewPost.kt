package com.codev.android

data class ResCreateNewPost(
    val code: Int,
    val result: MessageBodyPost
)

data class MessageBodyPost(
    val message: String
)
