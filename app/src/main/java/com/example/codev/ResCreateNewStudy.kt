package com.example.codev

data class ResCreateNewStudy(
    val code: Int,
    val result: MessageBody
)

data class MessageBody(
    val message: String
)
