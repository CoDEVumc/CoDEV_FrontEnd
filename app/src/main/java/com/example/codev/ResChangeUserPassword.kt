package com.example.codev

import com.google.gson.JsonObject

data class ResChangeUserPassword(
    val code: Int,
    val result: ChangePasswordMessage
)

data class ChangePasswordMessage(
    val message: String
)