package com.example.codev

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codev.databinding.FragmentChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment:Fragment() {
    private lateinit var viewBinding: FragmentChatBinding
    private lateinit var mainAppActivity: Context
    private lateinit var adapter: AdapterChatRoomList

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainAppActivity){
            mainAppActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("stomp join 본인 이메일",UserSharedPreferences.getKey(mainAppActivity))
        ChatClient.join(mainAppActivity, UserSharedPreferences.getKey(mainAppActivity))
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

    private fun setAdapter(dataList: ArrayList<ResponseOfGetChatRoomListData>, context: Context){
        adapter = AdapterChatRoomList(dataList, context){
            index: Int, itemCount: Int -> if(itemCount != -1) {
                Log.d("stomp 기존 채팅방 새로운 메세지", "$index, $itemCount")
                (mainAppActivity as Activity).runOnUiThread(Runnable { adapter.notifyItemRangeChanged(index,itemCount) })
            }else{
                Log.d("stomp 새로운 채팅방 새로운 메세지", "$index, $itemCount")
                (mainAppActivity as Activity).runOnUiThread(Runnable { adapter.notifyItemInserted(index) })
                viewBinding.chatRoomList.smoothScrollToPosition(index)
            }
        }
        ChatClient.setChatRoomAdapter(adapter)
        viewBinding.chatRoomList.adapter = adapter
    }

    private fun loadData(context: Context){
        RetrofitClient.service.getChatRoomList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object:
            Callback<ResGetChatRoomList> {
            override fun onResponse(call: Call<ResGetChatRoomList>, response: Response<ResGetChatRoomList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방 리스트 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 채팅방 리스트 불러오기 성공", "\n${it.toString()}")
                            setAdapter(it.result.complete, context)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetChatRoomList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }
}