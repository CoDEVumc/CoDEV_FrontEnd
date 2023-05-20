package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@SuppressLint("CheckResult")
object ChatClient{
    private const val BASE_URL = "ws://semtle.catholic.ac.kr:8080/ws-codev"
    private var mStompClient: StompClient
    private const val intervalMillis = 1000L
    private val oKClient = OkHttpClient()
    private lateinit var room: Disposable
    private lateinit var Client: Disposable
    private lateinit var adapterChatList: AdapterChatList
    private lateinit var adapterChatRoomList: AdapterChatRoomList

    init {
        mStompClient = StompClient(oKClient, intervalMillis)
        mStompClient.url = BASE_URL
        Client = mStompClient.connect().subscribe {
            Log.d("stomp connect: type", it.type.toString())
            Log.d("stomp connect: exception", it.exception.toString())
            when (it.type) {
                Event.Type.OPENED -> {
                    Log.d("stomp opened", Event.Type.OPENED.toString())
                }
                Event.Type.CLOSED -> {
                    Log.d("stomp closed", Event.Type.CLOSED.toString())
                }
                Event.Type.ERROR -> {
                    Log.d("stomp error", Event.Type.ERROR.toString())
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
                val pm = json.get("pm") as Boolean
                Log.d("stomp", json.toString())
                Log.d("stomp", type)
                if (type != "ENTER" && type != "LEAVE" && type != "TAB"){
                    adapterChatList.addData(ResponseOfGetChatListData(type,roomId,sender,content,createdDate,profileImg,co_nickName, pm))
                }else if(type == "TAB"){
                    Log.d("stomp TAB","TAB 타입 메세지 수신완료")
                    adapterChatRoomList.findRoomId(ResponseOfGetChatRoomListData(profileImg, profileImg.split("_")[0], co_nickName, sender, false, " ", " "," ", createdDate.toInt(), content, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1))
                }
            }catch (e: java.lang.Exception){
                Log.d("stomp join: 에러", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun setChatListAdapter(adapter: AdapterChatList){
        this.adapterChatList = adapter
    }

    fun setChatRoomAdapter(adapter: AdapterChatRoomList){
        this.adapterChatRoomList = adapter
    }

    fun exit(){
        room.dispose()
    }

    fun disconnect(){
        Client.dispose()
    }

    fun sendMessage(type: String, roomId: String, sender: String, msg: String){
        mStompClient.send("/app/chat/message", ChatMessage(type, roomId, sender, msg).toString()).subscribe(){
            Log.d("stomp: send 여부", it.toString())
//            if (it){ }
        }
    }

    fun createChatRoom(context: Context, roomTitle: String, inviteList: ArrayList<String>, chatType: String, postType: String ="", postId: Int = -1, user1: String = "", user2: String = ""){
        val roomId = createRoomId(context, chatType, postType, postId, user1, user2)

        if (roomId.isEmpty()){
            Log.d("chat","roomId 생성 실패")
            Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
        }else{
            RetrofitClient.service.createChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), ReqCreateChatRoom(roomId, chatType, title, null)).enqueue(object: Callback<JsonObject> {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("chat: 채팅방생성 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("chat: 채팅방생성 성공! ", "\n${it.toString()}")
                                    inviteChatRoom(context, roomId, roomTitle, inviteList)

                                }
                            }
                            401 ->{
                                Log.d("chat: 401", "이미 생성")
                                Toast.makeText(context, "이미 채팅방이 존재합니다, 해당 채팅방으로 이동합니다.", Toast.LENGTH_SHORT).show()
                                //생성된 채팅으로 이동
                                moveChatRoom(context, chatType, roomId, roomTitle, 3, 3)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test: 채팅방생성 실패", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun createRoomId(context: Context, chatType: String, postType: String ="", postId: Int = -1, user1: String = "", user2: String = ""): String {
        var roomId = ""
        var usr1 = ""
        var usr2 = ""
        if(user1 > user2){
            usr1 = user2
            usr2 = user1
        }else{
            usr1 = user1
            usr2 = user2
        }

        if (chatType == "UTU"){
            if(usr1.isEmpty() or usr2.isEmpty()){
                Log.d("chat","1대1 메세지 유저 설정 오류")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }else{
                roomId = "${chatType}_${usr1}_${usr2}"
            }
        }else if(chatType == "OTM"){
            if (postId != -1){
                roomId = "${chatType}_${postType}_${postId}"
            }
        }

        return roomId
    }

    private fun moveChatRoom(context: Context, chatType: String, roomId: String, roomTitle: String, roomPeople: Int, isRead: Int){
        var people = -1
        if (chatType == "UTU"){
            people = 1
        }else if(chatType == "OTM"){
            people = roomPeople
        }

        ChatClient.join(context, roomId)
        val intent = Intent(context, ChatRoomActivity::class.java)
        intent.putExtra("roomId", roomId)
        intent.putExtra("title", roomTitle)
        intent.putExtra("people", people)
        intent.putExtra("isRead", isRead)
        context.startActivity(intent)
    }

    fun inviteChatRoom(context: Context, chatType: String, roomId: String, roomTitle: String, inviteList: ArrayList<String>){
        RetrofitClient.service.inviteChat(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), ReqInviteChat(roomId, inviteList)).enqueue(object: Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("chat: 채팅방초대 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("chat: 채팅방초대 성공! ", "\n${it.toString()}")
                                moveChatRoom(context, chatType, roomId, roomTitle, inviteList.size,0)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("chat: 채팅방초대 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }



    fun getPeopleChatRoom(){

    }

    fun exitChatRoom(){

    }

    fun kickChatRoom(){

    }
}