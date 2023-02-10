package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.android.material.internal.ContextUtils.getActivity
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.*


@SuppressLint("CheckResult")
object ChatClient{
    private const val BASE_URL = "ws://semtle.catholic.ac.kr:8080/ws-codev"
    private var mStompClient: StompClient
    private const val intervalMillis = 1000L
    private val client = OkHttpClient()
    private lateinit var room: Disposable
    private lateinit var adapter: AdapterChatList

    init {
        mStompClient = StompClient(client, intervalMillis)
        mStompClient.url = BASE_URL
        mStompClient.connect().subscribe {
            Log.d("stomp connect: type", it.type.toString())
            Log.d("stomp connect: exception", it.exception.toString())
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
    }

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    fun join(context: Context, roomId: String){
        room = mStompClient.join("/topic/chat/room/$roomId").subscribe {
            try {
                val json = JSONObject(it)
                val type = json.get("type").toString()
                val roomId = json.get("roomId").toString()
                val sender = json.get("sender").toString()
                val content = json.get("content").toString()
                val createdDate = json.get("createdDate").toString()
                val profileImg = json.get("profileImg").toString()
                val co_nickName = json.get("co_nickName").toString()
                Log.d("stomp", json.toString())
                Log.d("stomp", type)
                adapter.addData(ResponseOfGetChatListData(type,roomId,sender,content,createdDate,profileImg,co_nickName))
            }catch (e: java.lang.Exception){
                Log.d("stomp join: 에러", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun setAdapter(adapter: AdapterChatList){
        this.adapter = adapter
    }

    fun exit(){
        room.dispose()
        Log.d("stomp: disconnect", "join 해지")
    }

    fun sendMessage(type: String, roomId: String, sender: String, msg: String){
        mStompClient.send("/app/chat/message", ChatMessage(type, roomId, sender, msg).toString()).subscribe(){
            Log.d("stomp: send 여부", it.toString())
//            if (it){ }
        }
    }
}