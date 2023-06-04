package com.codev.android

data class ResUserInfoChanged(
    val code: Int,
    val result: MessageBody3
)
data class MessageBody3(
    val message: String
)
