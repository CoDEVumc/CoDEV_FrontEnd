package com.example.codev

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.databinding.RecycleRecruitDetailStackBinding

class AdapterRecruitStack(var context: Context, var dataList: List<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder{
        return StackViewHolder(context, RecycleRecruitDetailStackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is  StackViewHolder -> {
                holder.bind(dataList[position],position)
            }
        }
    }

    inner class StackViewHolder(val context: Context, private val binding: RecycleRecruitDetailStackBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: String, position: Int){
            Glide.with(context)
                .load(data)
                .into(binding.image)
        }
    }
}