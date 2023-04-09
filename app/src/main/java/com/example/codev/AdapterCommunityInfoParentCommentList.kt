package com.example.codev

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecycleCommunityCommentBinding

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
            binding.date.text = data.createdAt.toString()
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
}