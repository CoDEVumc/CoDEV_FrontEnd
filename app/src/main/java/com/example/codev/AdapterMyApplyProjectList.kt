package com.example.codev

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.codev.databinding.RecycleRecruitListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterMyApplyProjectList(private val context: Context, private val listData: ArrayList<ApplyData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ProjectItemViewHolder(context, RecycleRecruitListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ProjectItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class ProjectItemViewHolder(val context: Context, private val binding: RecycleRecruitListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ApplyData, position: Int){
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
            binding.partlist.text = data.co_parts

            //북마크 : co_heart : Boolean <-- true면 채운 하트 / false면 안채운 하트 && 하트 하트 자체는 Selector로 바꾸기
            binding.heart.isChecked = listData[position].co_heart
            binding.heart.setOnClickListener {
                listData[position].co_heart = binding.heart.isChecked
                request(data.co_projectId)
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


    private fun request(co_projectId: Int){
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
}