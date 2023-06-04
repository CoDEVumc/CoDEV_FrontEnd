package com.codev.android.homepage

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codev.android.*
import com.codev.android.databinding.HomeHotPostBinding

class AdapterHomeStudy(private val context: Context, private val listData: List<SData>): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    inner class StudyItemViewHolder(val context: Context, private val viewBinding: HomeHotPostBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(data: SData, position: Int){


            Glide.with(context).load(data.co_mainImg).into(viewBinding.hotPostImage)

            //프로젝트 제목
            viewBinding.postTitle.text = data.co_title

            //프로젝트 디데이
            if(data.co_deadLine.toInt() == 0) {
                viewBinding.hotPostDday.text = "D-Day"
            }else if(data.co_process == "TEST"){
                viewBinding.hotPostDday.text = "심사중"
            }else if(data.co_process == "FIN"){
                viewBinding.hotPostDday.text = "모집 완료"
            } else {
                viewBinding.hotPostDday.text = "D-" + data.co_deadLine
            }

            //프로젝트 총 인원
            viewBinding.peopleNumber.text = data.co_total.toString()

            //기술 스택
            if (!data.co_languages.isNullOrBlank()){
                val languages = data.co_languages
                viewBinding.stack.adapter = AdapterRecruitStack(context,languages.split(","))
            }

            //프로젝트 모집 파트
            viewBinding.partName.text = data.co_part


            viewBinding.root.setOnClickListener {
                val intent = Intent(viewBinding.root.context, RecruitDetailActivity::class.java)
                intent.putExtra("id",data.co_studyId)
                intent.putExtra("type","STUDY")
                intent.putExtra("dday",viewBinding.hotPostDday.text)
                Log.d("test : 선택한 스터디 아이디", data.co_studyId.toString())
                startActivity(viewBinding.root.context, intent, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StudyItemViewHolder(context,
            HomeHotPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is StudyItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    override fun getItemCount(): Int = listData.size

}