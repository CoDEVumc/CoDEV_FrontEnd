package com.example.codev

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecyleviewPortfolioHeader1Binding
import com.example.codev.databinding.RecyleviewPortfolioItem1Binding

class PortfolioAdapter1(private val listData: List<ResPortFolio>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                PortfolioHeaderViewHolder(RecyleviewPortfolioHeader1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                PortfolioItemViewHolder(RecyleviewPortfolioItem1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
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
    inner class PortfolioItemViewHolder(private val binding: RecyleviewPortfolioItem1Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResPortFolio, position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = data.updatedAt
            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
            }
        }
    }

    //Header ViewHolder 객체
    inner class PortfolioHeaderViewHolder(private val binding: RecyleviewPortfolioHeader1Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.addPortfolio.setOnClickListener {
                Log.d("test","포트폴리오 추가")
            }
        }
    }


}

