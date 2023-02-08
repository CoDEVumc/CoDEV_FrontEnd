package com.example.codev

import android.annotation.SuppressLint
import android.util.Log
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.Message
import okhttp3.OkHttpClient
import org.json.JSONObject



@SuppressLint("CheckResult")
object ChatClient2{
    private const val BASE_URL = "ws://semtle.catholic.ac.kr:8080/ws-codev"
    private var mStompClient: StompClient
    private const val intervalMillis = 1000L
    private val client = OkHttpClient()

    init {
        mStompClient = StompClient(client, intervalMillis)
        mStompClient.url = BASE_URL
        val stompConnection = mStompClient.connect().subscribe {
            Log.d("stomp type", it.type.toString())
            Log.d("stomp exception", it.exception.toString())
        }
        val topic = mStompClient.join("/topic/chat/room/OTO_PROJECT_5_zxz4641@gmail.com").subscribe {
            Log.d("stomp subscribe", it)
        }
        mStompClient.send("/app/chat/message", ChatMessage("TALK","OTO_PROJECT_5_zxz4641@gmail.com","zxz4641@gmail.com","되요가 아니라 돼요인가요?").toString()).subscribe()

    }

    fun sendMessage(){

    }

}