package com.example.codev

import android.provider.ContactsContract.CommonDataKinds.Nickname
import java.lang.reflect.Constructor
import java.sql.Timestamp

data class ResGetChatRoomParticipants(
    val code: Int,
    val result: ResponseOfGetChatRoomParticipants
)

data class ResponseOfGetChatRoomParticipants(
    val complete: ArrayList<ResponseOfGetChatRoomParticipantsData>
)

data class ResponseOfGetChatRoomParticipantsData(
    val roomId: String,
    val co_email: String,
    val co_nickName: String,
    val profileImg: String,
    val status: Boolean,
    val pm: Boolean
)

