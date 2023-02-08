package com.example.codev

import android.annotation.SuppressLint
import android.util.Log
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import java.util.*


@SuppressLint("CheckResult")
object ChatClient{
    private const val BASE_URL = "ws://semtle.catholic.ac.kr:8080/ws-codev"
    private var mStompClient: StompClient
    private const val intervalMillis = 1000L
    private val client = OkHttpClient()
    private lateinit var room: Disposable

    init {
        mStompClient = StompClient(client, intervalMillis)
        mStompClient.url = BASE_URL
        mStompClient.connect().subscribe {
            Log.d("stomp type", it.type.toString())
            Log.d("stomp exception", it.exception.toString())
            when (it.type) {
                Event.Type.OPENED -> {

                }
                Event.Type.CLOSED -> {

                }
                Event.Type.ERROR -> {

                }
                else -> {}
            }
        }
        val topic = mStompClient.join("/topic/chat/room/OTO_PROJECT_5_zxz4641@gmail.com").subscribe {
            Log.d("stomp subscribe", it)
        }
        mStompClient.send("/app/chat/message", ChatMessage("TALK","OTO_PROJECT_5_zxz4641@gmail.com","zxz4641@gmail.com","되요가 아니라 돼요인가요? :"+ Random().nextInt(99)+1).toString()).subscribe()
    }

    fun join(roomId: String){
        room = mStompClient.join("/topic/chat/room/$roomId").subscribe {
            Log.d("stomp join", it)
        }
    }

    fun exit(){
        room.dispose()
    }

    fun sendMessage(type: String, roomId: String, sender: String, msg: String){
        mStompClient.send("/app/chat/message", ChatMessage(type, roomId, sender, msg).toString()).subscribe()
    }

}