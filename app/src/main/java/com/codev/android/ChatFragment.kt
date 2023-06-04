package com.codev.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codev.android.databinding.FragmentChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment:Fragment() {
    private lateinit var viewBinding: FragmentChatBinding
    private lateinit var mainAppActivity: Context
    private lateinit var adapter: com.codev.android.AdapterChatRoomList

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("stomp join 본인 이메일",UserSharedPreferences.getKey())
        ChatClient.join(mainAppActivity, UserSharedPreferences.getKey())
        Log.d("test","onResume")
        loadData(mainAppActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatClient.exit()
        Log.d("test", "다른 탭 이동")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentChatBinding.inflate(layoutInflater)
        viewBinding.toolbarChat.toolbar1.inflateMenu(R.menu.menu_toolbar_1)
        viewBinding.toolbarChat.toolbar1.title = ""
        viewBinding.toolbarChat.toolbarImg.setImageResource(R.drawable.logo_chat)

        return viewBinding.root
    }

    private fun setAdapter(context: Context, dataList: ArrayList<ResponseOfGetChatRoomListData>){
        adapter =
            com.codev.android.AdapterChatRoomList(context, dataList) { index: Int, itemCount: Int ->
                if (itemCount != -1) {
                    Log.d("stomp 기존 채팅방 새로운 메세지", "$index, $itemCount")
                    (mainAppActivity as Activity).runOnUiThread(Runnable {
                        adapter.notifyItemRangeChanged(
                            index,
                            itemCount
                        )
                    })
                } else {
                    Log.d("stomp 새로운 채팅방 새로운 메세지", "$index, $itemCount")
                    (mainAppActivity as Activity).runOnUiThread(Runnable {
                        adapter.notifyItemInserted(
                            index
                        )
                    })
                    viewBinding.chatRoomList.smoothScrollToPosition(index)
                }
            }
        ChatClient.setChatRoomAdapter(adapter)
        viewBinding.chatRoomList.adapter = adapter
    }

    private fun loadData(context: Context){
        CoroutineScope(Dispatchers.Main).launch {
            val listData = ChatClient.getChatRoomList(context)
            setAdapter(context, listData)
        }
    }
}