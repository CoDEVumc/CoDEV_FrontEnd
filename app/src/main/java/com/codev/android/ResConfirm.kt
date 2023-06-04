package com.codev.android

data class ResConfirm(
    val code: Int,
    val result: ResConfirmMessage
)

data class ResConfirmMessage(
    val message: String
)
