package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.ActivityChatRoomBinding
import com.google.android.material.internal.ContextUtils.getActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatRoomActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityChatRoomBinding
    private lateinit var roomId: String
    private lateinit var adapter: AdapterChatList

//    private var originalRecyclerViewBottom: Int = 0
//    private var recyclerViewHeight: Int = 0

    override fun onDestroy() {
        super.onDestroy()
        Log.d("stomp: etc", "다른 탭 이동")
        ChatClient.sendMessage("LEAVE", roomId, UserSharedPreferences.getKey(this),"LEAVE")
        ChatClient.exit()
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbarChat.toolbar4)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val title = intent.getStringExtra("title")
        val people = intent.getIntExtra("people", -1)
        roomId = intent.getStringExtra("roomId").toString()
        val isRead = intent.getIntExtra("isRead", -1)

        viewBinding.toolbarChat.toolbar4.title = ""

        viewBinding.toolbarChat.toolbarText1.text = title
        if (people == 2){
            viewBinding.toolbarChat.toolbarText2.text = "1"
        }else{
            viewBinding.toolbarChat.toolbarText2.text = people.toString()
        }

        if (roomId != null) {
            loadData(this, roomId, isRead, people)
        }

        viewBinding.etChat.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    enableSend(true)
                }else{
                    enableSend(false)
                }
            }
        })


//        viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                val screenHeight = viewBinding.root.height
//                val keyboardHeight = screenHeight - viewBinding.chatList.bottom
//
//                if (keyboardHeight > screenHeight * 0.15) {
//                    viewBinding.chatList.smoothScrollBy(0, keyboardHeight)
//                } else {
//                    viewBinding.chatList.smoothScrollBy(0, -viewBinding.chatList.height)
//                }
//            }
//        })

//        viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
//            val rect = Rect()
//            viewBinding.root.getWindowVisibleDisplayFrame(rect)
//            val screenHeight = viewBinding.root.rootView.height
//            val keyboardHeight = screenHeight - rect.bottom
//            if (keyboardHeight > screenHeight * 0.15) {
//                // Keyboard is open, scroll the RecyclerView up by the keyboard height
//                viewBinding.chatList.smoothScrollBy(0, keyboardHeight)
//            } else {
//                // Keyboard is closed, scroll the RecyclerView back to its original position
//                viewBinding.chatList.smoothScrollBy(0, -keyboardHeight)
//            }
//        }

//        viewBinding.root.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            Log.d("test","bottom : $bottom, oldBottom: $oldBottom 차이는  ${bottom-oldBottom}")
//            if (bottom < oldBottom) {
//                // Keyboard has gone up, scroll the recycler view down to show the latest chat message
//                viewBinding.chatList.smoothScrollBy(0, oldBottom - bottom)
//            } else if (bottom > oldBottom) {
//                // Keyboard has gone down, scroll the recycler view up to the original position
//                viewBinding.chatList.smoothScrollBy(0, oldBottom - bottom)
//            }
//        }

//        viewBinding.chatList.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
//            Log.d("test", "$bottom and $oldBottom")
//            if (bottom < oldBottom) {
//                viewBinding.chatList.smoothScrollBy(0, oldBottom - bottom)
//            }else{
////                if(!viewBinding.chatList.canScrollVertically(1)) {
////                    viewBinding.chatList.smoothScrollToPosition((viewBinding.chatList.adapter?.itemCount?: 1) - 1)
////                }else{
////                    viewBinding.chatList.smoothScrollBy(0, oldBottom - bottom)
////                }
//            }
//        }

//        viewBinding.chatList.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            val displayRect = Rect()
//            window.decorView.getWindowVisibleDisplayFrame(displayRect)
//            val screenHeight = window.decorView.rootView.height
//            val keyboardHeight = screenHeight - displayRect.bottom
//            if (keyboardHeight > 0) {
//                // keyboard is up
//                viewBinding.chatList.smoothScrollBy(0, -keyboardHeight)
//            } else {
//                // keyboard is down
//                viewBinding.chatList.smoothScrollBy(0, keyboardHeight)
//            }
//        }

        viewBinding.btnSend.setOnClickListener {
            ChatClient.sendMessage("TALK", roomId, UserSharedPreferences.getKey(this), viewBinding.etChat.text.toString())
            viewBinding.chatList.scrollToPosition((viewBinding.chatList.adapter?.itemCount ?: 1) - 1)
            viewBinding.etChat.text.clear()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setAdapter(dataList: ArrayList<ResponseOfGetChatListData>, context: Context, people: Int){
        if (dataList.isNullOrEmpty()){
            adapter = AdapterChatList(arrayListOf(), context, people){
                Log.d("stomp 추가후 데이터 크기", it.toString())
                runOnUiThread(Runnable { adapter.notifyItemInserted(it) })

                //최하단일때 새 메시지(TALK, INVITE, DAY, EXIT 들어오면 추가된 데이터로 스크롤이동
                if(!viewBinding.chatList.canScrollVertically(1)) {
                    viewBinding.chatList.smoothScrollToPosition(it - 1)
                }
            }
        }else{
            adapter = AdapterChatList(dataList, context, people){
                Log.d("stomp 추가후 데이터 크기", it.toString())
                runOnUiThread(Runnable { adapter.notifyItemInserted(it) })

                //최하단일때 새 메시지(TALK, INVITE, DAY, EXIT 들어오면 추가된 데이터로 스크롤이동
                if(!viewBinding.chatList.canScrollVertically(1)) {
                    viewBinding.chatList.smoothScrollToPosition(it - 1)
                }
            }
        }
        ChatClient.setAdapter(adapter)
        viewBinding.chatList.adapter = adapter
    }

    private fun loadData(context: Context, roomId: String, isRead: Int, people: Int){
        RetrofitClient.service.getChatList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), roomId).enqueue(object:
            Callback<ResGetChatList> {
            override fun onResponse(call: Call<ResGetChatList>, response: Response<ResGetChatList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 채팅방 불러오기 성공", "\n${it.toString()}")
                             if(!it.result.complete.isNullOrEmpty()){
                                 Log.d("stomp 불러온 데이터 크기", it.result.complete.size.toString())
                                 Log.d("stomp isRead", isRead.toString())
                                 setAdapter(it.result.complete, context, people)
                                 viewBinding.chatList.scrollToPosition(it.result.complete.size - (isRead + 1))
                             }else{
                                 setAdapter(arrayListOf(), context, people)
                             }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetChatList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun enableSend(boolean: Boolean){
        if (viewBinding.btnSend.isEnabled != boolean){
            viewBinding.btnSend.isEnabled = boolean
            viewBinding.btnSend.isSelected = boolean
        }
    }
}