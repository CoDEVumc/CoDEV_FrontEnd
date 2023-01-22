package com.example.codev

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.ItemProjectlistBinding

class ProjectAdapter(private val listData: List<PData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  PortfolioItemViewHolder(ItemProjectlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PortfolioItemViewHolder -> {
                listData
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class PortfolioItemViewHolder(private val binding: ItemProjectlistBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PData, position: Int){
            binding.ptitle.text = data.co_title
            binding.pdday.text = data.co_deadLine
            binding.pNum.text = data.co_totalnum

            //partlist
            //language


        }
    }
}