package com.example.codev

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecycleRecruitProjectBinding

import com.bumptech.glide.Glide

class AdapterProject(private val listData: ArrayList<PData>, context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            }
            else {binding.pdday.text = "D-" + data.co_deadLine}

            //프로젝트 총 인원
            binding.pNum.text = data.co_total.toString()

            //프로젝트 모집 파트
            binding.ppartlist.text = data.co_parts

            //하트 : co_heart : Boolean <-- true면 채운 하트 / false면 안채운 하트 && 하트 하트 자체는 Selector로 바꾸기
            if (data.co_heart == false){
                binding.pheart?.isSelected = binding.pheart?.isSelected!=true //state_selected
                binding.pheart.setOnClickListener { //누르면 눌린 상태로 바꾸기
                    binding.pheart?.isSelected = binding.pheart?.isSelected != true
                }
            }
            else {
                binding.pheart?.isSelected = binding.pheart?.isSelected!=false //state_selected
                binding.pheart.setOnClickListener { //누르면 눌린 상태로 바꾸기
                    binding.pheart?.isSelected = binding.pheart?.isSelected != false
                }
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
}