package com.codev.android

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.codev.android.addpage.AddPfPageActivity
import com.codev.android.addpage.DefaultPf
import com.codev.android.addpage.PfDetailActivity
import com.codev.android.databinding.RecyclePortfolioFooter2Binding
import com.codev.android.databinding.RecyclePortfolioItem2Binding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterPortfolio2(private val listData: ArrayList<PortFolio>, private val co_name: String, private val co_birth: String, private val co_gender: String, private val returnDeleteCount: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM = 1
    private val FOOTER = 2
    private var EDIT: Boolean = false
    private var DELETECOUNT: Int = 0
    private var isChecked: Array<Boolean> = Array(listData.size){false}
    private var deleteList: ArrayList<Int> = arrayListOf()

    fun setEdit(boolean: Boolean){
        EDIT = boolean
    }

    fun setDeleteCount(int: Int){
        DELETECOUNT = int
    }

    fun resetIsChecked(){
        Arrays.setAll(isChecked){false}
    }

    fun getIsChecked(): Array<Boolean>{
        return isChecked
    }

    fun resetDeleteList(){
        deleteList.clear()
    }

    fun getDeleteList(): ArrayList<Int>{
        return deleteList
    }

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            FOOTER ->
                PortfolioFooterViewHolder(RecyclePortfolioFooter2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                PortfolioItemViewHolder(RecyclePortfolioItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PortfolioFooterViewHolder -> {
                holder.bind()
            }
            is PortfolioItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size + 1

    //뷰 홀더 타입
    override fun getItemViewType(position: Int): Int {
        return when(position){
            itemCount - 1 -> FOOTER
            else -> ITEM
        }
    }

    //Item의 ViewHolder 객체
    inner class PortfolioItemViewHolder(private val binding: RecyclePortfolioItem2Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PortFolio, position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = convertTimestampToDate(data.updatedAt)
            binding.chkDelete.isChecked = isChecked[position]
            binding.portfolio.isSelected = isChecked[position]
            changeEditMode(EDIT)

            binding.chkDelete.setOnClickListener {
                isChecked[position] = binding.chkDelete.isChecked
                if(binding.chkDelete.isChecked){
                    deleteList.add(data.co_portfolioId)
                }else{
                    deleteList.remove(data.co_portfolioId)
                }
                deleteCounter()
                notifyItemChanged(position)
            }

            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
                if (EDIT){
                    binding.chkDelete.isChecked = !binding.chkDelete.isChecked
                    isChecked[position] = binding.chkDelete.isChecked
                    if(binding.chkDelete.isChecked){
                        deleteList.add(data.co_portfolioId)
                    }else{
                        deleteList.remove(data.co_portfolioId)
                    }
                    deleteCounter()
                    notifyItemChanged(position)
                }else{
                    //포트폴리오 세부 페이지 이동
                    val intent = Intent(binding.portfolio.context, PfDetailActivity::class.java)
                    intent.putExtra("id", data.co_portfolioId.toString())
                    Log.d("test",data.co_portfolioId.toString())
                    ContextCompat.startActivity(binding.portfolio.context, intent, null)
                }
            }
        }

        private fun changeEditMode(boolean: Boolean){
            binding.chkDelete.isGone = !boolean
            binding.temp.isGone = !boolean
        }

        private fun deleteCounter() {
            if (binding.chkDelete.isChecked){
                DELETECOUNT += 1
                Log.d("test", "삭제할 항목수: $DELETECOUNT")
                if (DELETECOUNT == 1){
                    Log.d("test","리콜")
                    returnDeleteCount(DELETECOUNT)
                }
            }else{
                DELETECOUNT -= 1
                Log.d("test", "삭제할 항목수: $DELETECOUNT")
                if (DELETECOUNT == 0){
                    Log.d("test","리콜")
                    returnDeleteCount(DELETECOUNT)
                }
            }
        }
    }

    //Footer ViewHolder 객체
    inner class PortfolioFooterViewHolder(private val binding: RecyclePortfolioFooter2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            //포트폴리오 추가 기능 필요
            binding.addPortfolio.setOnClickListener {
                Log.d("test", "포트폴리오 추가")
                val intent = Intent(binding.addPortfolio.context, AddPfPageActivity::class.java)
                intent.putExtra("default", DefaultPf(co_name, co_birth, co_gender))
                ContextCompat.startActivity(binding.addPortfolio.context, intent, null)
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
