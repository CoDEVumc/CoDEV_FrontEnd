package com.codev.android

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.codev.android.addpage.AddPfPageActivity
import com.codev.android.addpage.DefaultPf
import com.codev.android.addpage.PfDetailActivity
import com.codev.android.databinding.RecyclePortfolioHeader1Binding
import com.codev.android.databinding.RecyclePortfolioItem1Binding
import java.sql.Timestamp
import java.text.SimpleDateFormat

class AdapterPortfolio1(private val listData: ArrayList<PortFolio>, private val co_name: String, private val co_birth: String, private val co_gender: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                PortfolioHeaderViewHolder(RecyclePortfolioHeader1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                PortfolioItemViewHolder(RecyclePortfolioItem1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
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
    inner class PortfolioItemViewHolder(private val binding: RecyclePortfolioItem1Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PortFolio, position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = convertTimestampToDate(data.updatedAt)
            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
                val intent = Intent(binding.portfolio.context, PfDetailActivity::class.java)
                intent.putExtra("id", data.co_portfolioId.toString())
                Log.d("test",data.co_portfolioId.toString())
                startActivity(binding.portfolio.context, intent, null)
            }
        }
    }

    //Header ViewHolder 객체
    inner class PortfolioHeaderViewHolder(private val binding: RecyclePortfolioHeader1Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.addPortfolio.setOnClickListener {
                Log.d("test","포트폴리오 추가")
                val intent = Intent(binding.addPortfolio.context, AddPfPageActivity::class.java)
                intent.putExtra("default", DefaultPf(co_name, co_birth, co_gender))
                startActivity(binding.addPortfolio.context, intent, null)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimestampToDate(timestamp: Timestamp):String {
        val sdf = SimpleDateFormat("yyyy.MM.dd")
        val date = sdf.format(timestamp)
        Log.d("TTT UNix Date -> ", sdf.format((System.currentTimeMillis())).toString())
        Log.d("TTTT date -> ", date.toString())
        return date.toString()
    }
}

