package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.databinding.RecycleChatRoomListBinding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterChatRoomList(private val listData: ArrayList<PortFolio>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatRoomListViewHolder(RecycleChatRoomListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ChatRoomListViewHolder -> {
                holder.bind(listData[position], position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class ChatRoomListViewHolder(private val binding: RecycleChatRoomListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PortFolio, position: Int){
//            Glide.with(context)
//                .load().circleCrop()
//                .into(binding.oneImg1)
//            binding.roomTitle.text =
//
//                binding.roomMemberNumber.text =
//                binding.roomMessage.text =
//                binding.roomChatDate.text =

        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimestampToDate(timestamp: Timestamp):String {
        val sdf = SimpleDateFormat("yyyy.MM.dd")
        val date = sdf.format(timestamp)
        Log.d("TTT UNix Date -> ", sdf.format((System.currentTimeMillis())).toString())
        Log.d("TTTT date -> ", date.toString())
        return date.toString()
    }
}
