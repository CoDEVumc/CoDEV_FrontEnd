package com.example.codev

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecyleviewPortfolioItemBinding
import com.example.codev.databinding.RecyleviwPortfolioHeaderBinding

class PortfolioAdapter(private val listData: List<ResPortFolio>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                PortfolioHeaderViewHolder(RecyleviwPortfolioHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                PortfolioItemViewHolder(RecyleviewPortfolioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PortfolioHeaderViewHolder -> {
                holder.bind()
            }
            is PortfolioItemViewHolder -> {
                listData
                holder.bind(listData[position-1],position-1)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size + 1

    //뷰 홀더 타입
    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> HEADER
            else -> ITEM
        }
    }

    //Item의 ViewHolder 객체
    inner class PortfolioItemViewHolder(private val binding: RecyleviewPortfolioItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResPortFolio,position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = data.updatedAt
            binding.portfolio.setOnClickListener {
                Log.d("test","포트폴리오 클릭 "+ position.toString())
            }
        }
    }

    //Header View체older 객체
    inner class PortfolioHeaderViewHolder(private val binding: RecyleviwPortfolioHeaderBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.addPortfolio.setOnClickListener {
                Log.d("test","포트폴리오 추가")
            }
        }
    }


}

