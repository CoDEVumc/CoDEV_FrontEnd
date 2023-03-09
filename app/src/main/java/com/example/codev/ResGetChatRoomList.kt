package com.example.codev

import java.sql.Timestamp

data class ResGetChatRoomList(
    val code: Int,
    val result: ResponseOfGetChatRoomList
)

data class ResponseOfGetChatRoomList(
    val complete: ArrayList<ResponseOfGetChatRoomListData>
)

data class ResponseOfGetChatRoomListData(
    val roomId: String,
    val room_type: String,
    val room_title: String,
    val mainImg: String,
    val status: Boolean,
    val receiverCo_email: String,
    val receiverCo_nickName: String,
    val receiverProfileImg: String,
    val people: Int,
    var latestconv: String,
    var latestDate: String,
    var isRead: Int
)

