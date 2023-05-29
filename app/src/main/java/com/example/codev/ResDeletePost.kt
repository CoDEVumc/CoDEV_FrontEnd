package com.example.codev

data class ResDeletePost(
    val code: Int,
    val result: ResDeletePostMessage
)

data class ResDeletePostMessage(
    val Complete: String
)
