package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.databinding.RecycleChatRoomListBinding
import io.reactivex.disposables.Disposable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AdapterChatRoomList(private val listData: ArrayList<ResponseOfGetChatRoomListData>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        @SuppressLint("SetTextI18n")
        fun bind(data: ResponseOfGetChatRoomListData, position: Int){

            var title = ""
            if (data.room_type == "OTM"){
                Glide.with(itemView.context)
                    .load(data.mainImg).circleCrop()
                    .into(binding.oneImg1)

                //채팅방에 TEMP랑 둘이 있을 경우
                if(data.people == 2){
                    binding.roomMemberNumber.text = 0.toString()
                }else{
                    binding.roomMemberNumber.text = (data.people -1).toString()
                }

                title = data.room_title
                binding.roomTitle.text = title

            }else if (data.room_type == "OTO"){
                Glide.with(itemView.context)
                    .load(data.receiverProfileImg).circleCrop()
                    .into(binding.oneImg1)

                //채팅방에 TEMP랑 둘이 있을 경우
                if(data.people == 2){
                    binding.roomMemberNumber.text = 0.toString()
                    title = "상대방이 대화방에서 나갔습니다"
                }else{
                    binding.roomMemberNumber.text = (data.people -2).toString()
                    title = data.receiverCo_nickName
                }
                binding.roomTitle.text = title

            }

            binding.roomMessage.text = data.latestconv
            if (data.latestDate.isNullOrBlank()){
                binding.roomChatDate.text = " "
            }else{
                binding.roomChatDate.text = stringToTime(data.latestDate)
            }
            if (data.isRead == 0){
                binding.roomChatAlarm.isGone = true
            }else{
                if (data.isRead <= 100){
                    binding.roomChatAlarm.text = data.isRead.toString()
                }
                else if (data.isRead in 101..900){
                    binding.roomChatAlarm.text = "100+"
                }else{
                    binding.roomChatAlarm.text = "900+"
                }
            }

            binding.room.setOnClickListener {
                ChatClient.join(itemView.context, data.roomId)
                val intent = Intent(itemView.context, ChatRoomActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("roomId", data.roomId)
                intent.putExtra("people", binding.roomMemberNumber.text.toString().toInt())
                intent.putExtra("isRead", data.isRead)
                itemView.context.startActivity(intent)
            }
        }
    }

    private fun stringToTime(string: String): String{
        val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val convertTime = LocalDateTime.parse(string, formatter1)
        val currentTime = LocalDateTime.now()
        val formatter2 = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return if (convertTime.format(formatter2) == currentTime.format(formatter2)){
            convertTime.format(DateTimeFormatter.ofPattern("a h:mm"))
        }else{
            convertTime.format(formatter2)
        }
    }
}
