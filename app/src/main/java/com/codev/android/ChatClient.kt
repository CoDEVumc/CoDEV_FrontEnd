package com.codev.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("CheckResult")
object ChatClient{
    private const val BASE_URL = "ws://semtle.catholic.ac.kr:8080/ws-codev"
    private var mStompClient: StompClient
    private const val intervalMillis = 1000L
    private val oKClient = OkHttpClient()
    private lateinit var room: Disposable
    private lateinit var Client: Disposable
    private lateinit var adapterChatList: com.codev.android.AdapterChatList
    private lateinit var adapterChatRoomList: com.codev.android.AdapterChatRoomList

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
                val id = json.get("roomId").toString()
                val sender = json.get("sender").toString()
                val content = json.get("content").toString()
                val createdDate = json.get("createdDate").toString()
                val profileImg = json.get("profileImg").toString()
                val coNickName = json.get("co_nickName").toString()
                val pm = json.get("pm") as Boolean
                Log.d("stomp", json.toString())
                Log.d("stomp", type)
                if (type != "ENTER" && type != "LEAVE" && type != "TAB"){
                    adapterChatList.addData(ResponseOfGetChatListData(type,id,sender,content,createdDate,profileImg,coNickName, pm))
                }else if(type == "TAB"){
                    Log.d("stomp TAB","TAB 타입 메세지 수신완료")
                    adapterChatRoomList.findRoomId(ResponseOfGetChatRoomListData(profileImg, profileImg.split("_")[0], coNickName, sender, false, " ", " "," ", createdDate.toInt(), content, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1))
                }
            }catch (e: java.lang.Exception){
                Log.d("stomp join: 에러", e.toString())
                e.printStackTrace()
            }
        }
    }

    fun setChatListAdapter(adapter: com.codev.android.AdapterChatList){
        this.adapterChatList = adapter
    }

    fun setChatRoomAdapter(adapter: com.codev.android.AdapterChatRoomList){
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

    private fun createRoomId(context: Context, chatType: String, postType: String ="", postId: Int = -1, user1: String = "", user2: String = ""): String {
        var roomId = ""

        if (chatType == "UTU"){
            val id1 = user1.split("@")[0]
            val id2 = user2.split("@")[0]
            var usr1 = ""
            var usr2 = ""
            if(id1 > id2){
                usr1 = id2
                usr2 = id1
            }else{
                usr1 = id1
                usr2 = id2
            }
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

    fun createChatRoom(context: Context, roomTitle: String, mainImg: String? = null, inviteList: ArrayList<String>, chatType: String, postType: String = "", postId: Int = -1, user1: String = "", user2: String = "", optionMove: Boolean = true){
        val roomId = createRoomId(context, chatType, postType, postId, user1, user2)

        if (roomId.isEmpty()){
            Log.d("chat","roomId 생성 실패")
            Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
        }else{
            RetrofitClient.service.createChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), ReqCreateChatRoom(roomId, chatType, roomTitle, mainImg)).enqueue(object: Callback<JsonObject> {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful){
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    //채팅방 생성 후 초대 및 해당 채팅으로 이동
                                    Log.d("chat: 채팅방생성 성공! ", "\n${it.toString()}")
                                    inviteChatRoom(context, chatType, roomId, roomTitle, inviteList, optionMove)
                                }
                            }

                        }
                    }else{
                        when(response.code()){
                            //생성되어 있던 채팅으로 이동
                            401 ->{
                                Toast.makeText(context, "이미 채팅방이 존재합니다, 해당 채팅방으로 이동합니다.", Toast.LENGTH_SHORT).show()
                                response.errorBody()?.string()?.let{
                                    try {
                                        Log.d("chat: 401(이미 존재하는 채팅방)", "\n${it}")
                                        val json = JSONObject(it)
                                        val message = json.get("message")
                                        val data = JSONObject(message.toString())
                                        val roomType = data.getString("room_type")
                                        var title = ""
                                        var roomPeople = data.getInt("people")
                                        val isRead = data.getInt("isRead")
                                        if (roomType == "OTM"){
                                            if (roomPeople == 2){
                                                roomPeople = 0
                                                title = "상대방이 대화방에서 나갔습니다"
                                            }else{
                                                roomPeople -= 1
                                                title = data.getString("room_title")
                                            }
                                            moveChatRoom(context, roomType, roomId, title, roomPeople, isRead)
                                        }else{
                                            if (roomPeople == 2){
                                                roomPeople = 0
                                                title = "상대방이 대화방에서 나갔습니다"
                                            }else{
                                                roomPeople = 1
                                                title = data.getString("receiverCo_nickName")
                                            }
                                            moveChatRoom(context, roomType, roomId, title, roomPeople, isRead)
                                        }
                                    }catch (e: JSONException) {
                                        Log.d("chat: 401(이미 존재하는 채팅방)", e.toString())
                                    }
                                }
                            }
                            else ->{
                                Log.d("chat: 채팅방생성 실패",response.toString())
                                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
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

    fun moveChatRoom(context: Context, chatType: String, roomId: String, roomTitle: String, roomPeople: Int, isRead: Int){
        ChatClient.join(context, roomId)
        val intent = Intent(context, ChatRoomActivity::class.java)
        intent.putExtra("roomId", roomId)
        intent.putExtra("title", roomTitle)
        intent.putExtra("people", roomPeople)
        intent.putExtra("isRead", isRead)
        context.startActivity(intent)
    }

    fun inviteChatRoom(context: Context, chatType: String, roomId: String, roomTitle: String, inviteList: ArrayList<String>, optionMove: Boolean = true){
        RetrofitClient.service.inviteChat(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), ReqInviteChat(roomId, inviteList)).enqueue(object: Callback<JsonObject> {
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
                                if (optionMove){
                                    moveChatRoom(context, chatType, roomId, roomTitle, inviteList.size,0)
                                }
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

    fun renameChatRoom(context: Context, chatType: String, roomId: String, roomTitle: String, people: Int, optionMove: Boolean = true){
        RetrofitClient.service.renameChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), ReqRenameChatRoom(roomId, roomTitle)).enqueue(object:
            Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("chat: 채팅방 이름 변경 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("chat: 채팅방 이름 변경 성공! ", "\n${it.toString()}")
                                if (optionMove){
                                    moveChatRoom(context, chatType, roomId, roomTitle, people, 0)
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("chat: 채팅방 이름 변경 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    suspend fun getChatRoomList(context: Context): ArrayList<ResponseOfGetChatRoomListData> = withContext(Dispatchers.Default){
        val listData: ArrayList<ResponseOfGetChatRoomListData> = ArrayList()

        try {
            // Retrofit의 비동기 호출을 suspend 함수로 래핑하여 사용합니다.
            val response = RetrofitClient.service.getChatRoomList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())).execute()

            if (response.isSuccessful.not()) {
                Log.d("chat: 채팅 리스트 불러오기 실패", response.toString())
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            Log.d("chat: 채팅 리스트 불러오기 성공", "\n${it.toString()}")
                            listData.addAll(it.result.complete)
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Log.d("chat: 채팅 리스트 불러오기 실패", "[Fail]${t.toString()}")
        }

        return@withContext listData
    }

    suspend fun getChatDataList(context: Context, roomId: String): ArrayList<ResponseOfGetChatListData> = withContext(Dispatchers.Default) {
        val listData: ArrayList<ResponseOfGetChatListData> = ArrayList()

        try {
            // Retrofit의 비동기 호출을 suspend 함수로 래핑하여 사용합니다.
            val response = RetrofitClient.service.getChatList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), roomId).execute()

            if (response.isSuccessful.not()) {
                Log.d("chat: 채팅 목록 불러오기 실패", response.toString())
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            Log.d("chat: 채팅 목록 불러오기 성공", "\n${it.toString()}")
                            listData.addAll(it.result.complete)
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Log.d("chat: 채팅 목록 불러오기 실패", "[Fail]${t.toString()}")
        }

        return@withContext listData
    }

    suspend fun getParticipants(context: Context, roomId: String): ArrayList<ResponseOfGetChatRoomParticipantsData> = withContext(Dispatchers.Default) {
        val listData: ArrayList<ResponseOfGetChatRoomParticipantsData> = ArrayList()

        try {
            // Retrofit의 비동기 호출을 suspend 함수로 래핑하여 사용합니다.
            val response = RetrofitClient.service.getChatRoomParticipants(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), roomId).execute()

            if (response.isSuccessful.not()) {
                Log.d("chat: 채팅방 참여인원 불러오기 실패", response.toString())
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            Log.d("chat: 채팅방 참여인원 불러오기 성공", "\n${it.toString()}")
                            listData.addAll(it.result.complete)
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Log.d("chat: 채팅방 참여인원 불러오기 실패", "[Fail]${t.toString()}")
        }

        return@withContext listData
    }

    fun exitChatRoom(){

    }

    fun kickChatRoom(){

    }
}