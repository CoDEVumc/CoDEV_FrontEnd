package com.example.codev

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.RecycleRecruitProjectBinding

class AdapterProject(private val listData: ArrayList<PData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            binding.pdday.text = "D" + data.co_deadLine

            //프로젝트 총 인원
            binding.pNum.text = data.co_total.toString()

            //프로젝트 모집 파트
            binding.ppartlist.text = data.co_parts

            //하트 : co_heart : Boolean <-- true면 채운 하트 / false면 안채운 하트 && 하트 하트 자체는 Selector로 바꾸기

            //프로젝트 사용 스택
//            val languages = data.co_languages
//            val comma = ","
//            val imageList = languages.split(comma)
//            val img1 = imageList[0]
//            val img2 = imageList[1]
//            val img3 = imageList[2]
//            val img4 = imageList[3]
//            val img5 = imageList[4]



        }
    }
}