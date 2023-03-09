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
import com.bumptech.glide.Glide
import com.example.codev.addpage.AddPfPageActivity
import com.example.codev.addpage.AppliedDetailActivity
import com.example.codev.addpage.DefaultPf
import com.example.codev.addpage.PfDetailActivity
import com.example.codev.databinding.*
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//[바디] ITEM(체크 → 선택 & 선택 취소하기) : from 토글헤더 -> 선택취소 / from 토글아이템 -> 선택
class AdapterRecruitApplicants2(private val context: Context, private val listData: ArrayList<ApplicantInfoData>, private val id: Int, private val type: String, private val returnCount: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isChecked: Array<Boolean> = Array(listData.size){false}
    private var EDIT: Boolean = false
    private var COUNT: Int = 0
    private var selectList: ArrayList<String> = arrayListOf()

    private var partList: ArrayList<ApplicantData> = arrayListOf()

    fun setEdit(boolean: Boolean){
        EDIT = boolean
    }

    fun setCount(int: Int){
        COUNT = int
    }

    fun resetIsChecked(){
        Arrays.setAll(isChecked){false}
    }

    fun resetSelectList(){
        selectList.clear()
    }

    fun getIsChecked(): Array<Boolean>{
        return isChecked
    }

    fun getSelectList(): ArrayList<String>{
        return selectList
    }

    fun getListData(): ArrayList<ApplicantInfoData>{
        return listData
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
    inner class ApplicantItemViewHolder(private val binding: RecycleApplicantItem1Binding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(data: ApplicantInfoData, position: Int){

            Glide.with(itemView.context)
                .load(data.profileImg).circleCrop()
                .into(binding.applyerProfile)

            binding.applyerNum.text = (position + 1).toString()
            binding.applyerPortfolioTitle.text = data.co_title
            binding.applyerName.text = data.co_name
            binding.applyerPart.text = data.co_part

            binding.chkChoose.isChecked = isChecked[position]
            binding.applicantPortfolio.isSelected = isChecked[position]

            changeEditMode(EDIT)

            binding.chkChoose.setOnClickListener {
                isChecked[position] = binding.chkChoose.isChecked
                if(binding.chkChoose.isChecked){
                    selectList.add(data.co_email)
                }else{
                    selectList.remove(data.co_email)
                }
            }

            //포트폴리오 상세조회 & EDIT
            binding.applicantPortfolio.setOnClickListener {
                Log.d("AdapterRecruitApplyList: ", "포트폴리오 클릭 $position")
                if (EDIT){
                    binding.applicantPortfolio.isSelected = true
                    binding.chkChoose.isChecked = !binding.chkChoose.isChecked
                    isChecked[position] = binding.chkChoose.isChecked
                    if(binding.chkChoose.isChecked){
                        selectList.add(data.co_email)
                    }else{
                        selectList.remove(data.co_email)
                    }
                    //선택취소
                    counter()
                    notifyItemChanged(position)
                }else{
                    //포트폴리오 세부 페이지 이동
                    val intent = Intent(binding.portfolio.context, AppliedDetailActivity::class.java)
                    intent.putExtra("type",type)
                    intent.putExtra("id", id)
                    intent.putExtra("coPortfolioId", data.co_portfolioId)
                    intent.putExtra("coPart", data.co_part)
                    intent.putExtra("coTemporaryStorage", data.co_temporaryStorage)
                    intent.putExtra("name", data.co_name)
                    intent.putExtra("receiver_email", data.co_email)
                    startActivity(binding.portfolio.context, intent, null)
                }
            }

            binding.chkChoose.setOnClickListener {
                binding.applicantPortfolio.isSelected= binding.chkChoose.isChecked
                isChecked[position] = binding.chkChoose.isChecked
                if(binding.chkChoose.isChecked){
                    selectList.add(data.co_email)
                }else{
                    selectList.remove(data.co_email)
                }
                counter()
                notifyItemChanged(position)
            }



        }

        private fun changeEditMode(boolean: Boolean){
            binding.chkChoose.isGone = !boolean
            binding.applyerNum.isGone = boolean
        }

        //COUNT <= 파트별 제한인원 일때만 체크가 되어야 함
        private fun counter() {
            if (binding.chkChoose.isChecked){
                COUNT += 1
                Log.d("counter()", "선택한 항목수: $COUNT")
                if (COUNT == 1){
                    Log.d("test","리콜")
                    returnCount(COUNT)
                }
            }else{
                COUNT -= 1
                Log.d("counter()", "선택한 항목수: $COUNT")
                if (COUNT == 0){
                    Log.d("test","리콜")
                    returnCount(COUNT)
                }
            }
        }
    }
}

