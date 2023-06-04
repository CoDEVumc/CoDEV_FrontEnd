package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codev.databinding.ActivityChatRoomBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatRoomActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityChatRoomBinding
    private lateinit var roomId: String
    private lateinit var adapter: AdapterChatList
    private var recyclerViewPosition = 0

    override fun onPause() {
        super.onPause()
        ChatClient.sendMessage("LEAVE", roomId, UserSharedPreferences.getKey(),"LEAVE")
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
        viewBinding.toolbarChat.toolbarText2.text = people.toString()

        loadData(this, roomId, isRead)

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


        viewBinding.chatList.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if((viewBinding.chatList.adapter?.itemCount ?: 0) != 0){
                if (bottom < oldBottom) {
                    // Keyboard has gone up, save the position of the recycler view
                    recyclerViewPosition = (viewBinding.chatList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    Log.d("test 보여지는 아이템","$recyclerViewPosition")
                    Log.d("test 전체 아이템", viewBinding.chatList.adapter?.itemCount.toString())
//                viewBinding.chatList.smoothScrollToPosition(viewBinding.chatList.adapter?.itemCount ?: 0)
                    viewBinding.chatList.smoothScrollBy(0, oldBottom - bottom)
                } else if (bottom > oldBottom) {
                    // Keyboard has gone down, restore the original position of the recycler view
                    viewBinding.chatList.smoothScrollToPosition(recyclerViewPosition)
                }
            }
        }

        viewBinding.btnSend.setOnClickListener {
            ChatClient.sendMessage("TALK", roomId, UserSharedPreferences.getKey(), viewBinding.etChat.text.toString())
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

    private fun setAdapter(context: Context, dataList: ArrayList<ResponseOfGetChatListData>, isRead: Int){
        adapter = AdapterChatList(context, dataList){
            Log.d("stomp 추가후 데이터 크기", it.toString())
            runOnUiThread(Runnable { adapter.notifyItemInserted(it) })
            //최하단일때 새 메시지(TALK, INVITE, DAY, EXIT 들어오면 추가된 데이터로 스크롤이동
            if(!viewBinding.chatList.canScrollVertically(1)) {
                viewBinding.chatList.smoothScrollToPosition(it - 1)
            }
            recyclerViewPosition = (viewBinding.chatList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }

        ChatClient.setChatListAdapter(adapter)
        viewBinding.chatList.adapter = adapter
        ChatClient.sendMessage("ENTER", roomId, UserSharedPreferences.getKey(), "ENTER")

        if (dataList.size != 0){
            viewBinding.chatList.scrollToPosition(dataList.size - (isRead + 1))
        }
    }

    private fun loadData(context: Context, roomId: String, isRead: Int){
        CoroutineScope(Dispatchers.Main).launch {
            val listData = ChatClient.getChatDataList(context, roomId)
            setAdapter(context, listData, isRead)
        }
    }

    private fun enableSend(boolean: Boolean){
        if (viewBinding.btnSend.isEnabled != boolean){
            viewBinding.btnSend.isEnabled = boolean
            viewBinding.btnSend.isSelected = boolean
        }
    }
}