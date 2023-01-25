package com.example.codev

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

//import com.bumptech.glide.Glide
import com.example.codev.databinding.RecycleRecruitStudyBinding

class AdapterStudy(private val listData: ArrayList<SData>, context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  StudyItemViewHolder(RecycleRecruitStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is StudyItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class StudyItemViewHolder(private val binding: RecycleRecruitStudyBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: SData, position: Int){
            binding.sTitle.text = data.co_title
            binding.sDday.text = "D" + data.co_deadLine
            binding.sNum.text = data.co_total.toString()
            binding.spartlist.text = data.co_parts

//            val languages = data.co_languages
//            val comma = ","
//            val imageList = languages.split(comma)
//            val defaultImage = R.drawable.ic_launcher_background
//            val img1 = imageList[0]
//            val img2 = imageList[1]
//            val img3 = imageList[2]
//            val img4 = imageList[3]
//            val img5 = imageList[4]

//            Glide.with(this@StudyAdapter)
//                .load(img1) //불러올 이미지 url
//                .placeholder(defaultImage)
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.sStackImg1)
//            Glide.with(this@StudyAdapter)
//                .load(img2) //불러올 이미지 url
//                .placeholder(defaultImage)
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.sStackImg2)
//            Glide.with(this@StudyAdapter)
//                .load(img3) //불러올 이미지 url
//                .placeholder(defaultImage)
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.sStackImg3)
//            Glide.with(this@StudyAdapter)
//                .load(img4) //불러올 이미지 url
//                .placeholder(defaultImage)
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.sStackImg4)
//            Glide.with(this@StudyAdapter)
//                .load(img5) //불러올 이미지 url
//                .placeholder(defaultImage)
//                .error(defaultImage)
//                .fallback(defaultImage)
//                .circleCrop()
//                .into(binding.sStackImg5)


        }
    }
}