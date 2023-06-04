package com.codev.android

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codev.android.databinding.RecycleCommunityDetailImageBinding

import com.codev.android.databinding.RecycleCommunityQuestionAndInfoListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterCommunityQnaPhotoList(private val context: Context, private val listData: ArrayList<QnaDetailPhoto>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  QnaItemViewHolder(context, RecycleCommunityDetailImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is QnaItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class QnaItemViewHolder(val context: Context, private val binding: RecycleCommunityDetailImageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: QnaDetailPhoto, position: Int){
            Glide.with(itemView.context)
                .load(data.co_fileUrl)
                .into(binding.image)
        }
    }
}