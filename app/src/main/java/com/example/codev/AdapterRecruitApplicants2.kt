package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.addpage.AddPfPageActivity
import com.example.codev.addpage.DefaultPf
import com.example.codev.addpage.PfDetailActivity
import com.example.codev.databinding.*
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//[바디_선택하기 안누름]
//[바디] ITEM(체크 → 선택 & 선택 취소하기) : from 토글헤더 -> 선택취소 / from 토글아이템 -> 선택
class AdapterRecruitApplicants2(private val context: Context, private val listData: ArrayList<ApplicantInfoData>, private val returnDeleteCount: (Int) -> Unit, private val returnRegistCount: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isChecked: Array<Boolean> = Array(listData.size){false}

    private var EDIT: Boolean = false
    private var DELETE: Boolean = false
    private var SELECT: Boolean = false

    private var DELETECOUNT: Int = 0
    private var deleteList: ArrayList<Int> = arrayListOf()
    private var REGISTCOUNT: Int = 0
    private var registList: ArrayList<Int> = arrayListOf()

    fun setEdit(boolean: Boolean){
        EDIT = boolean
    }
    fun setDelete(boolean: Boolean){
        DELETE = boolean
    }
    fun setSelect(boolean: Boolean){
        SELECT = boolean
    }

    fun setDeleteCount(int: Int){
        DELETECOUNT = int
    }
    fun setRegistCount(int: Int){
        REGISTCOUNT = int
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
    fun resetRegistList(){
        registList.clear()
    }

    fun getDeleteList(): ArrayList<Int>{
        return deleteList
    }
    fun getRegistList(): ArrayList<Int>{
        return registList
    }

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApplicantItemViewHolder(RecycleApplicantItem1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ApplicantItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    //chk_choose(체크버튼)--applyer_num(지원자position) <-activity에서 해야될듯???????????
    inner class ApplicantItemViewHolder(private val binding: RecycleApplicantItem1Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ApplicantInfoData, position: Int){
            binding.applyerNum.text = position.toString()
            //binding.applyerProfile.setImageURI(data.profileImg) //지원자 프로필사진
            when(data.co_temporaryStorage){ //선택여부
                //img_selected(선택된 지원자 한테만 보이게)
                true -> binding.imgSelected.isGone = false //선택된 지원자면 imgSelected.isGone = false
                false -> binding.imgSelected.isGone = true //선택안된 지원자면 imgSelected.isGone = true
            }
            binding.applyerPortfolioTitle.text = data.co_title
            binding.applyerName.text = data.co_name
            binding.applyerPart.text = data.co_part

            binding.chkChoose.isChecked = isChecked[position]
            binding.portfolio.isSelected = isChecked[position]

            //선택 vs 선택취소
            changeEditMode(EDIT)

            //선택취소 vs 선택
            binding.chkChoose.setOnClickListener {
                //선택취소
                isChecked[position] = binding.chkChoose.isChecked
                if(binding.chkChoose.isChecked){
                    deleteList.add(data.co_portfolioId)
                }else{
                    deleteList.remove(data.co_portfolioId)
                }

                //선택

            }

            //포트폴리오 상세조회 & EDIT
            binding.portfolio.setOnClickListener {
                binding.portfolio.isSelected = true
                Log.d("AdapterRecruitApplyList: ", "포트폴리오 클릭 $position")
                if (EDIT){
                    binding.chkChoose.isChecked = !binding.chkChoose.isChecked
                    isChecked[position] = binding.chkChoose.isChecked
                    if(binding.chkChoose.isChecked){
                        deleteList.add(data.co_portfolioId)
                    }else{
                        deleteList.remove(data.co_portfolioId)
                    }
                    //선택취소
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
            binding.chkChoose.isGone = !boolean
            binding.applyerNum.isGone = boolean
        }
        private fun changeDeleteMode(boolean: Boolean){
            binding.chkChoose.isGone = !boolean
        }
        private fun changeSelectMode(boolean: Boolean){
            binding.chkChoose.isGone = !boolean
        }

        private fun deleteCounter() {
            if (binding.chkChoose.isChecked){
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
        private fun registCounter() {
            if (binding.chkChoose.isChecked){
                REGISTCOUNT += 1
                Log.d("test", "선택할 항목수: $REGISTCOUNT")
                if (REGISTCOUNT == 1){
                    Log.d("test","리콜")
                    returnRegistCount(REGISTCOUNT)
                }
            }else{
                REGISTCOUNT -= 1
                Log.d("test", "선택할 항목수: $REGISTCOUNT")
                if (REGISTCOUNT == 0){
                    Log.d("test","리콜")
                    returnRegistCount(REGISTCOUNT)
                }
            }
        }
    }




}

