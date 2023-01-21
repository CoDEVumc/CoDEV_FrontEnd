package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecyleviewPortfolioFooter2Binding
import com.example.codev.databinding.RecyleviewPortfolioHeader2Binding
import com.example.codev.databinding.RecyleviewPortfolioItem2Binding
import java.util.*

class PortfolioAdapter2(private val context:Context, private val listData: List<ResPortFolio>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1
    private val FOOTER = 2
    private var EDIT: Boolean = false
    private var DELETECOUNT: Int = 0
    private var isChecked: Array<Boolean> = Array(listData.size){false}

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                PortfolioHeaderViewHolder(RecyleviewPortfolioHeader2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            FOOTER ->
                PortfolioFooterViewHolder(RecyleviewPortfolioFooter2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                PortfolioItemViewHolder(RecyleviewPortfolioItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PortfolioHeaderViewHolder -> {
                holder.bind()
            }
            is PortfolioFooterViewHolder -> {
                holder.bind()
            }
            is PortfolioItemViewHolder -> {
                holder.bind(listData[position-1],position-1)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size + 2

    //뷰 홀더 타입
    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> HEADER
            itemCount - 1 -> FOOTER
            else -> ITEM
        }
    }

    //Item의 ViewHolder 객체
    inner class PortfolioItemViewHolder(private val binding: RecyleviewPortfolioItem2Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResPortFolio, position: Int){
            binding.portfolioTitle.text = data.co_title
            binding.portfolioUpdatedAt.text = data.updatedAt
            binding.chkDelete.isChecked = isChecked[position]
            binding.portfolio.isSelected = isChecked[position]
            if (EDIT){
                binding.chkDelete.isGone = false
                binding.temp.isGone = false
            }else{
                binding.chkDelete.isGone = true
                binding.temp.isGone = true
            }

            binding.chkDelete.setOnClickListener {
                isChecked[position] = binding.chkDelete.isChecked
                checkDelete()
                notifyItemChanged(position+1)
            }

            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
                if (EDIT){
                    binding.chkDelete.isChecked = !binding.chkDelete.isChecked
                    isChecked[position] = binding.chkDelete.isChecked
                    checkDelete()
                    notifyItemChanged(position+1)
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun checkDelete() {
            if (binding.chkDelete.isChecked){
                DELETECOUNT += 1
                Log.d("test", "삭제할 항목수: $DELETECOUNT")
                if (DELETECOUNT == 1){
                    notifyDataSetChanged()
                }
            }else{
                DELETECOUNT -= 1
                Log.d("test", "삭제할 항목수: $DELETECOUNT")
                if (DELETECOUNT == 0){
                    notifyDataSetChanged()
                }
            }
        }
    }

    //Header ViewHolder 객체
    inner class PortfolioHeaderViewHolder(private val binding: RecyleviewPortfolioHeader2Binding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("NotifyDataSetChanged")
        fun bind(){
            binding.btnDelete.isGone = !binding.btnEditChk.isChecked
            if (DELETECOUNT>0){
                enableDelete(true)
                binding.btnDeleteText.setTextColor(ContextCompat.getColor(context,R.color.black_700))
            }else{
                enableDelete(false)
                binding.btnDeleteText.setTextColor(ContextCompat.getColor(context,R.color.black_300))
            }

            binding.btnEdit.setOnClickListener {
                binding.btnEditChk.isChecked = !binding.btnEditChk.isChecked
                EDIT = binding.btnEditChk.isChecked
                DELETECOUNT = 0
                Arrays.setAll(isChecked){false}
                notifyDataSetChanged()
            }

            binding.btnEditChk.setOnClickListener {
                EDIT = binding.btnEditChk.isChecked
                DELETECOUNT = 0
                Arrays.setAll(isChecked){false}
                notifyDataSetChanged()
            }

            //isChecked 된 항목들 삭제기능 필요
            binding.btnDelete.setOnClickListener {
                Log.d("test", "삭제")
            }

            //isChecked 된 항목들 삭제기능 필요
            binding.btnDeleteImg.setOnClickListener {
                Log.d("test", "삭제")
            }
        }

        @SuppressLint("ResourceAsColor")
        private fun enableDelete(boolean: Boolean){
            binding.btnDelete.isEnabled = boolean
            binding.btnDeleteImg.isEnabled = boolean
            binding.btnDeleteImg.isSelected = boolean
        }
    }

    //Footer ViewHolder 객체
    inner class PortfolioFooterViewHolder(private val binding: RecyleviewPortfolioFooter2Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){
            //포트폴리오 추가 기능 필요
            binding.addPortfolio.setOnClickListener {
                Log.d("test", "포트폴리오 추가")
            }
        }
    }


}
