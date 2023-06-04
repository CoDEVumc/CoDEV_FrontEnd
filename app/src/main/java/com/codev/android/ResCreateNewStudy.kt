package com.codev.android

data class ResCreateNewStudy(
    val code: Int,
    val result: MessageBody
)

data class MessageBody(
    val message: String
)
