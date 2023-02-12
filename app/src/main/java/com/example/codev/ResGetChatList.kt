package com.example.codev

import android.provider.ContactsContract.CommonDataKinds.Nickname
import java.lang.reflect.Constructor
import java.sql.Timestamp

data class ResGetChatList(
    val code: Int,
    val result: ResponseOfGetChatList
)

data class ResponseOfGetChatList(
    val complete: ArrayList<ResponseOfGetChatListData>
)

data class ResponseOfGetChatListData(
    val type: String,
    val roomId: String,
    val sender: String,
    val content: String,
    val createdDate: String,
    val profileImg: String,
    val co_nickName: String,
    val pm: Boolean
)

