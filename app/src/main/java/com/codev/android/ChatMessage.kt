package com.codev.android

import java.sql.Timestamp

data class ChatMessage(
    val type: String,
    val roomId: String,
    val sender: String,
    val content: String
){
    override fun toString(): String {
        return "{\"type\": \"$type\", \"roomId\": \"$roomId\", \"sender\": \"$sender\", \"content\": \"$content\"}"
    }
}