package com.example.codev

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecyclePortfolioItem3Binding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterPortfolio3(private val listData: ArrayList<PortFolio>, private var selectedIdx: Int, private val returnData: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isChecked: Array<Boolean> = Array(listData.size){false}

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PortfolioItemViewHolder(RecyclePortfolioItem3Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PortfolioItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class PortfolioItemViewHolder(private val binding: RecyclePortfolioItem3Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PortFolio, position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = convertTimestampToDate(data.updatedAt)
            binding.chkDelete.isChecked = isChecked[position]
            binding.portfolio.isSelected = isChecked[position]

            binding.chkDelete.setOnClickListener {
                if(selectedIdx != -1 && selectedIdx != adapterPosition){ //이미 클릭한 다른 항목을 선택 해제하기
                    isChecked[selectedIdx] = false
                    notifyItemChanged(selectedIdx)
                }
                selectedIdx = adapterPosition

                isChecked[position] = binding.chkDelete.isChecked
                notifyItemChanged(adapterPosition)

                if(isChecked[position]){
                    returnData(data.co_portfolioId)
                }else{
                    returnData(-1)
                }
            }

            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
                if(selectedIdx != -1 && selectedIdx != adapterPosition){ //이미 클릭한 다른 항목을 선택 해제하기
                    isChecked[selectedIdx] = false
                    notifyItemChanged(selectedIdx)
                }
                selectedIdx = adapterPosition

                binding.chkDelete.isChecked = !binding.chkDelete.isChecked
                isChecked[position] = binding.chkDelete.isChecked
                notifyItemChanged(adapterPosition)

                if(isChecked[position]){
                    returnData(data.co_portfolioId)
                }else{
                    returnData(-1)
                }
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
