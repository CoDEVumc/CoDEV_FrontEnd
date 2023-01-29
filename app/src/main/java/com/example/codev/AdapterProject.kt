package com.example.codev

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecycleRecruitProjectBinding

import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterProject(private val listData: ArrayList<PData>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ProjectItemViewHolder(RecycleRecruitProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
    inner class ProjectItemViewHolder(private val binding: RecycleRecruitProjectBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: PData, position: Int){
            //프로젝트 제목
            binding.ptitle.text = data.co_title

            //프로젝트 디데이
            if(data.co_deadLine.toInt() == 0) {
                binding.pdday.text = "D-Day"
            }
            else if(data.co_deadLine.toInt() < 0){ //기간 지남

                val deadline = data.co_deadLine
                val dday = deadline.substring(1,deadline.length)

                binding.pdday.text = "D+" + dday

                //binding.pdday.text = "-END-"
            }
            else {binding.pdday.text = "D-" + data.co_deadLine}

            //프로젝트 총 인원
            binding.pNum.text = data.co_total.toString()

            //프로젝트 모집 파트
            binding.ppartlist.text = data.co_parts

            //북마크 : co_heart : Boolean <-- true면 채운 하트 / false면 안채운 하트 && 하트 하트 자체는 Selector로 바꾸기
            binding.pheart.isChecked = listData[position].co_heart
            binding.pheart.setOnClickListener {
                listData[position].co_heart = binding.pheart.isChecked
                request(data.co_projectId)
            }



            //프로젝트 사용 스택 이미지 5개
//            val langs = data.co_languages
//            val comma = ","
//            val imgList = langs.split(comma)
//            val defaultImage = R.drawable.profiles
//            val img1 = imgList[0]
//            val img2 = imgList[1]
//            val img3 = imgList[2]
//            val img4 = imgList[3]
//            val img5 = imgList[4]


            //.svg파일 띄우기
//            Glide.with(context)
//                .load(img1) //불러올 이미지 url
//                .placeholder(defaultImage) //미리보기
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.pStackImg1)

//            Glide.with(RecruitProjectFragment())
//                .load(img2) //불러올 이미지 url
//                .placeholder(defaultImage) //미리보기
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.pStackImg2)
//            Glide.with(RecruitProjectFragment())
//                .load(img3) //불러올 이미지 url
//                .placeholder(defaultImage) //미리보기
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.pStackImg3)
//            Glide.with(RecruitProjectFragment())
//                .load(img4) //불러올 이미지 url
//                .placeholder(defaultImage) //미리보기
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.pStackImg4)
//            Glide.with(RecruitProjectFragment())
//                .load(img5) //불러올 이미지 url
//                .placeholder(defaultImage) //미리보기
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.pStackImg5)



        }
    }


    private fun request(co_projectId: Int){
        RetrofitClient.service.requestProjectBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),co_projectId).enqueue(object:Callback<JsonObject>{
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