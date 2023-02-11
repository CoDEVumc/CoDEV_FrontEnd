package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.addpage.AddPfPageActivity
import com.example.codev.addpage.DefaultPf
import com.example.codev.addpage.PfDetailActivity
import com.example.codev.databinding.*
import com.google.gson.JsonObject
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat

//[토글] : HEADER(상세조회), ITEM(상세조회)
class AdapterRecruitApplicants1(private val context: Context, private val listData: ArrayList<ApplicantData>, private val returnData: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1

    private var isChecked: Array<Boolean> = Array(listData.size){false}

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                ApplicantHeaderViewHolder(RecycleRecruitApplyPartHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                ApplicantItemViewHolder(RecycleRecruitApplyPartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ApplicantHeaderViewHolder -> {
                holder.bind(listData[position])
            }
            is ApplicantItemViewHolder -> {
                holder.bind(listData[position-1],position-1)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size + 1

    //뷰 홀더 타입
    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> HEADER
            else -> ITEM
        }
    }

    //Item의 ViewHolder 객체 [토글]
    inner class ApplicantItemViewHolder(private val binding: RecycleRecruitApplyPartItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ApplicantData, position: Int){
            binding.part.text = data.co_part
            binding.num.text = data.co_applicantsCount.toString() + "명" //현재 지원자 n <- data.co_applicantsCount

            binding.partToggle.setOnClickListener{
                binding.partToggle.isSelected = true
                Log.d("AdapterRecruitApplyList: ", "분야 토글 클릭 $position")
                returnData(binding.part.toString()) //토글아이템 클릭 시 "프론트엔드, 백엔드, 기획, 디자인, 기타" 넘겨줌
            }
        }
    }

    //Header ViewHolder 객체 [토글]
    inner class ApplicantHeaderViewHolder(private val binding: RecycleRecruitApplyPartHeaderBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: ApplicantData){
            binding.num.text = data.co_limit.toString() + "명"

            binding.partToggle.setOnClickListener {
                Log.d("ApaterRecruitApplyList: ","선택한 프로젝트 지원자 조회: ")
                returnData("TEMP") //토글헤더 클릭 시 TEMP 넘겨줌
            }

            //현재 선택된 지원자 n <- data.co_tempSavedApplicantsCount



//            binding.addPortfolio.setOnClickListener {
//                Log.d("test","포트폴리오 추가")
//                val intent = Intent(binding.addPortfolio.context, AddPfPageActivity::class.java)
//                intent.putExtra("default", DefaultPf(co_name, co_birth, co_gender))
//                startActivity(binding.addPortfolio.context, intent, null)
//            }
        }
    }







}

