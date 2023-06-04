package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.codev.databinding.RecycleRecruitListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterRecruitSearchList(private val context: Context, private val listPData: ArrayList<PData>, private val listSData: ArrayList<SData>, private val now: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val PROJECT = 0
    private val STUDY = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            PROJECT -> {
                ProjectItemViewHolder(context,RecycleRecruitListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            STUDY -> {
                StudyItemViewHolder(context,RecycleRecruitListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else ->{
                ProjectItemViewHolder(context,RecycleRecruitListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ProjectItemViewHolder-> {
                holder.bind(listPData[position], position)
            }
            is StudyItemViewHolder -> {
                holder.bind(listSData[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return when(now){
            0 -> listPData.size
            1 -> listSData.size
            else -> listPData.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(now){
            0 -> PROJECT
            1 -> STUDY
            else -> PROJECT
        }
    }


    inner class ProjectItemViewHolder(val context: Context, private val binding: RecycleRecruitListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PData, position: Int){
            //프로젝트 제목
            binding.title.text = data.co_title

            //프로젝트 디데이
            if(data.co_deadLine.toInt() == 0) {
                binding.dday.text = "D-Day"
            }else if(data.co_process == "TEST"){
                binding.dday.text = "심사중"
            }else if(data.co_process == "FIN"){
                binding.dday.text = "모집 완료"
            } else {
                binding.dday.text = "D-" + data.co_deadLine
            }

            //프로젝트 총 인원
            binding.num.text = data.co_total.toString()

            //기술 스택
            if (!data.co_languages.isNullOrBlank()){
                val languages = data.co_languages
                binding.stack.adapter = AdapterRecruitStack(context,languages.split(","))
            }

            //프로젝트 모집 파트
            var before_string = data.co_parts
            var after_string = before_string.replace(",", "  ")
            //binding.partlist.text = data.co_parts
            binding.partlist.text = after_string

            //북마크 : co_heart : Boolean <-- true면 채운 하트 / false면 안채운 하트 && 하트 하트 자체는 Selector로 바꾸기
            binding.heart.isChecked = listPData[position].co_heart
            binding.heart.setOnClickListener {
                listPData[position].co_heart = binding.heart.isChecked
                projectBookMark(itemView.context, data.co_projectId)
            }

            binding.item.setOnClickListener {
                val intent = Intent(binding.item.context, RecruitDetailActivity::class.java)
                intent.putExtra("id",data.co_projectId)
                intent.putExtra("type","PROJECT")
                intent.putExtra("dday",binding.dday.text)
                Log.d("test : 선택한 프로젝트 아이디", data.co_projectId.toString())
                startActivity(binding.item.context,intent,null)
            }
        }
    }


    private fun projectBookMark(context: Context, co_projectId: Int){
        RetrofitClient.service.requestProjectBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),co_projectId).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: AdapterP__request() 성공! ", "\n${it.toString()}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: AdapterP__request()실패 : ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    inner class StudyItemViewHolder(val context: Context, private val binding: RecycleRecruitListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: SData, position: Int){
            //스터디 제목
            binding.title.text = data.co_title

            //스터디 디데이
            if(data.co_deadLine.toInt() == 0) {
                binding.dday.text = "D-Day"
            }else if(data.co_process == "TEST"){
                binding.dday.text = "심사중"
            }else if(data.co_process == "FIN"){
                binding.dday.text = "모집 완료"
            } else {
                binding.dday.text = "D-" + data.co_deadLine
            }

            //스터디 총 인원
            binding.num.text = data.co_total.toString()

            //기술 스택
            if (!data.co_languages.isNullOrBlank()){
                val languages = data.co_languages
                binding.stack.adapter = AdapterRecruitStack(context,languages.split(","))
            }

            //스터디 분야
            binding.partlist.text = data.co_part

            //북마크
            binding.heart.isChecked = listSData[position].co_heart
            binding.heart.setOnClickListener {
                listSData[position].co_heart = binding.heart.isChecked
                studyBookMark(itemView.context, data.co_studyId)
            }

            binding.item.setOnClickListener {
                val intent = Intent(binding.item.context, RecruitDetailActivity::class.java)
                intent.putExtra("id",data.co_studyId)
                intent.putExtra("type","STUDY")
                intent.putExtra("dday",binding.dday.text)
                Log.d("test : 선택한 스터디 아이디", data.co_studyId.toString())
                startActivity(binding.item.context, intent, null)
            }
        }
    }

    private fun studyBookMark(context: Context, co_studyId: Int){
        RetrofitClient.service.requestStudyBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),co_studyId).enqueue(object:
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: AdapterS__request() 성공! ", "\n${it.toString()}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: AdapterS__request()실패 : ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

}