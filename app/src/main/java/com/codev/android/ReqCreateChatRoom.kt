package com.codev.android

data class ReqCreateChatRoom(
    val roomId : String,
    val room_type : String,
    val room_title: String,
    val mainImg: String?
)
