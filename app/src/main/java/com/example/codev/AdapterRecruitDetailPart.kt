package com.example.codev

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.databinding.RecycleRecruitDetailPartBinding

class AdapterRecruitDetailPart(val context: Context, val dataList: ArrayList<RecruitPartLimit>): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder{
        return PartViewHolder(RecycleRecruitDetailPartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PartViewHolder -> {
                holder.bind(dataList[position],position)
            }
        }
    }

    inner class PartViewHolder(private val binding: RecycleRecruitDetailPartBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: RecruitPartLimit, position: Int){
            binding.part.text = data.co_part
            binding.num.text = data.co_limit.toString()
        }
    }
}