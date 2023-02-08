//package com.example.codev
//
//import android.annotation.SuppressLint
//import android.util.Log
//import org.json.JSONObject
//import ua.naiksoftware.stomp.Stomp
//import ua.naiksoftware.stomp.StompClient
//
//
//@SuppressLint("CheckResult")
//object ChatClient{
//    private val BASE_URL = "ws://semtle.catholic.ac.kr:8080/ws-codev"
//    private var mStompClient: StompClient
//
//    init {
//        Log.d("stomp","stomp 싱글톤")
//        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, BASE_URL)
//        mStompClient.connect()
//        mStompClient.topic( "/topic/chat/room/OTO_PROJECT_5_zxz4641@gmail.com").subscribe{
//            Log.d("stomp", it.toString())
//
//            Log.d("stomp : payload", it.payload)
//
//            val json = JSONObject(it.payload)
//
//            when(val type: String = json.get("type").toString()){
//                "TALK"->{
//                    Log.d("stomp : talk", type)
//                }
//                "ENTER"->{
//                    Log.d("stomp : enter", type)
//                }
//                "LEAVE"->{
//                    Log.d("stomp : leave", type)
//                }
//                "EXIT"->{
//                    Log.d("stomp : exit", type)
//                }
//            }
//        }
//        mStompClient.send("/app/chat/message", ChatMessage("TALK","OTO_PROJECT_5_zxz4641@gmail.com","zxz4641@gmail.com","hello world").toString()).subscribe()
//        //mStompClient.disconnect()
//        Log.d("stomp","stomp 꺼짐")
//    }
//
//    fun sendMessage(){
//
//    }
//
//}