package com.example.codev

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.codev.databinding.RecycleChatItemDateBinding
import com.example.codev.databinding.RecycleChatItemInviteBinding
import com.example.codev.databinding.RecycleChatItemMyContinueBinding
import com.example.codev.databinding.RecycleChatItemMyFirstBinding
import com.example.codev.databinding.RecycleChatItemOtherContinueBinding
import com.example.codev.databinding.RecycleChatItemOtherFirstBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class AdapterChatList(private val context: Context, private val listData: ArrayList<ResponseOfGetChatListData>, private val returnDataListSize: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val MY = 1
    private val MY_CONTINUE = 2
    private val OTHER = 3
    private val OTHER_CONTINUE = 4
    private val DAY = 5
    private val INVITE = 6
    private val EXIT = 7


    fun addData(data: ResponseOfGetChatListData){
        if (data.type != "ENTER" && data.type != "LEAVE"){
            listData.add(data)
            returnDataListSize(listData.size)
        }
    }


//    "EXIT", "INVITE", "LEAVE", "TALK"(분류해서 내거, 상대방거, 연속 유무), "ENTER"


    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MY -> {
                ChatMyFirstViewHolder(RecycleChatItemMyFirstBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            MY_CONTINUE ->{
                ChatMyContinueViewHolder(RecycleChatItemMyContinueBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            OTHER -> {
                ChatOtherFirstViewHolder(RecycleChatItemOtherFirstBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            OTHER_CONTINUE ->{
                ChatOtherContinueViewHolder(RecycleChatItemOtherContinueBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            DAY ->{
                ChatDayViewHolder(RecycleChatItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            INVITE ->{
                ChatInviteViewHolder(RecycleChatItemInviteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> {
                ChatOtherFirstViewHolder(RecycleChatItemOtherFirstBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ChatOtherFirstViewHolder -> {
                holder.bind(listData[position], position)
            }
            is ChatMyFirstViewHolder -> {
                holder.bind(listData[position], position)
            }
            is ChatOtherContinueViewHolder -> {
                holder.bind(listData[position], position)
            }
            is ChatMyContinueViewHolder -> {
                holder.bind(listData[position], position)
            }
            is ChatDayViewHolder -> {
                holder.bind(listData[position], position)
            }
            is ChatInviteViewHolder -> {
                holder.bind(listData[position], position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    override fun getItemViewType(position: Int): Int {
        if (listData[position].type == "DAY"){
            return DAY
        }else if(listData[position].type == "INVITE"){
            return INVITE
        }else if (UserSharedPreferences.getKey() == listData[position].sender) {
            if(position == 0){
                return MY
            }else if ((listData[position-1].type == "TALK") && (listData[position-1].sender == listData[position].sender) && checkContinue(listData[position-1].createdDate, listData[position].createdDate)){
                return MY_CONTINUE
            }else{
                return MY
            }
        }else{
            if(position == 0){
                return OTHER
            }else if ((listData[position-1].type == "TALK") && (listData[position-1].sender == listData[position].sender) && checkContinue(listData[position-1].createdDate, listData[position].createdDate)){
                return OTHER_CONTINUE
            }else{
                return OTHER
            }
        }
    }

    //Item의 ViewHolder 객체
    inner class ChatOtherFirstViewHolder(private val binding: RecycleChatItemOtherFirstBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseOfGetChatListData, position: Int){
            Log.d("stomp data type",data.type)
            Glide.with(itemView.context)
                .load(data.profileImg).circleCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        CoroutineScope(Dispatchers.Main).launch {
                            Glide.with(itemView.context)
                                .load("http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png")
                                .circleCrop()
                                .into(binding.profileImg)
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                })
                .into(binding.profileImg)

            binding.chat.text = data.content
            binding.nickname.text = data.co_nickName
            binding.time.text = stringToTime(data.createdDate)
            binding.profileHost.isGone = !data.pm
        }
    }

    inner class ChatOtherContinueViewHolder(private val binding: RecycleChatItemOtherContinueBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseOfGetChatListData, position: Int){
            Log.d("stomp data type",data.type)
            binding.chat.text = data.content
            binding.time.isGone = true
//            binding.time.text = stringToTime(data.createdDate)
        }
    }

    inner class ChatMyFirstViewHolder(private val binding: RecycleChatItemMyFirstBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseOfGetChatListData, position: Int){
            Log.d("stomp data type",data.type)
            binding.chat.text = data.content
            binding.time.text = stringToTime(data.createdDate)
        }
    }

    inner class ChatMyContinueViewHolder(private val binding: RecycleChatItemMyContinueBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseOfGetChatListData, position: Int){
            Log.d("stomp data type",data.type)
            binding.chat.text = data.content
            binding.time.isGone = true
//            binding.time.text = stringToTime(data.createdDate)
        }
    }

    inner class ChatDayViewHolder(private val binding: RecycleChatItemDateBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseOfGetChatListData, position: Int){
            binding.day.text = data.content
        }
    }

    inner class ChatInviteViewHolder(private val binding: RecycleChatItemInviteBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseOfGetChatListData, position: Int){
            binding.content.text = data.content
        }
    }

    private fun stringToTime(string: String): String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val convertTime = LocalDateTime.parse(string, formatter)
        return convertTime.format(DateTimeFormatter.ofPattern("a h:mm"))
    }

    private fun checkContinue(string1: String, string2: String): Boolean{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val convertTime1 = LocalDateTime.parse(string1, formatter)
        val convertTime2 = LocalDateTime.parse(string2, formatter)
        return (convertTime2.minute - convertTime1.minute) < 1
    }
}
