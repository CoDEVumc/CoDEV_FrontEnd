package com.codev.android

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.codev.android.databinding.RecycleCommunityCommentBinding
import com.codev.android.databinding.RecycleCommunityCommentReplyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterCommunityInfoChildCommentList(private val context: Context, private val listData: ArrayList<InfoDetailChildComment>, private val viewerEmail: String, private val sendChildId: (id: Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  InfoItemViewHolder(context, RecycleCommunityCommentReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
    inner class InfoItemViewHolder(val context: Context, private val binding: RecycleCommunityCommentReplyBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: InfoDetailChildComment, position: Int){
            binding.nickname.text = data.co_nickname
            binding.content.text = data.content
            binding.date.text = stringToTime(data.createdAt.toString())
            if (data.co_email != viewerEmail){
                binding.btnMore.visibility = View.GONE
            }
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

            binding.btnMore.setOnClickListener {
                sendChildId(data.co_rcoib)
            }
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