package com.example.codev

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecylePortfolioHeader1Binding
import com.example.codev.databinding.RecylePortfolioItem1Binding

class PortfolioAdapter1(private val listData: ArrayList<PortFolio>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                PortfolioHeaderViewHolder(RecylePortfolioHeader1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                PortfolioItemViewHolder(RecylePortfolioItem1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
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
    inner class PortfolioItemViewHolder(private val binding: RecylePortfolioItem1Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PortFolio, position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = data.updatedAt
            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
            }
        }
    }

    //Header ViewHolder 객체
    inner class PortfolioHeaderViewHolder(private val binding: RecylePortfolioHeader1Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.addPortfolio.setOnClickListener {
                Log.d("test","포트폴리오 추가")
            }
        }
    }

}

