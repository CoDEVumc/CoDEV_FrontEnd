package com.example.codev

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.codev.databinding.RecycleCommunityCommentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterCommunityInfoParentCommentList(private val context: Context, private val listData: ArrayList<InfoDetailComment>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  InfoItemViewHolder(context, RecycleCommunityCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is InfoItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class InfoItemViewHolder(val context: Context, private val binding: RecycleCommunityCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: InfoDetailComment, position: Int){
            val adapter = AdapterCommunityInfoChildCommentList(context, data.coReCommentOfInfoBoardList)
            binding.rvComment.adapter = adapter
            binding.nickname.text = data.co_nickname
            binding.content.text = data.content
            binding.date.text = stringToTime(data.createdAt.toString())
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
        }
    }

    fun setViewMode(boolean: Boolean){
        if (boolean){
            //뷰어와 댓글 작성자 같을 때 : 작성자
            Log.d("test","작성자 모드")
        }else{
            //뷰어와 댓글 작성자 다를 때 : 뷰어
            Log.d("test","뷰어 모드")
        }
    }

    private fun stringToTime(string: String) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        val convertTime = LocalDateTime.parse(string, formatter)
        return convertTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
    }
}