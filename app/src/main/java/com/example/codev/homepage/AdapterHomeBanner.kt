package com.example.codev.homepage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.R
import com.example.codev.databinding.RecycleRecruitDetailImageBinding

class AdapterHomeBanner(var context: Context, var dataList: ArrayList<HomeBannerItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder{
        return PagerViewHolder(context, RecycleRecruitDetailImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PagerViewHolder -> {
                holder.bind(dataList[position],position)
            }
        }
    }

    inner class PagerViewHolder(val context: Context, private val binding: RecycleRecruitDetailImageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: HomeBannerItem, position: Int){
            Glide.with(context)
                .load(data.imgUrl)
                .error(R.drawable.test_error_image)
                .into(binding.image)
            binding.image.scaleType = ImageView.ScaleType.CENTER_CROP

            //이미지 전체화면 구현 필요
            binding.image.setOnClickListener {
                Log.d("test", "이미지 클릭 $position")
            }
        }
    }
}