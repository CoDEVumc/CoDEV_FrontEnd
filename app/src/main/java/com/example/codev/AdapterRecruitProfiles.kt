package com.example.codev

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.databinding.RecycleRecruitChatProfileBinding
import com.example.codev.databinding.RecycleRecruitDetailStackBinding

class AdapterRecruitProfiles(private var context: Context, private var dataList: ArrayList<ApplicantInfoData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder{
        return ProfileViewHolder(context, RecycleRecruitChatProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is  ProfileViewHolder -> {
                holder.bind(dataList[position],position)
            }
        }
    }

    inner class ProfileViewHolder(val context: Context, private val binding: RecycleRecruitChatProfileBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ApplicantInfoData, position: Int){
            if(itemCount > 4){

                Glide.with(itemView.context)
                    .load(data.profileImg).circleCrop()
                    .into(binding.applyerProfile)
            }


            Glide.with(itemView.context)
                .load(data.profileImg).circleCrop()
                .into(binding.applyerProfile)
            Log.d("AdapterRecruitProfiles :", binding.applyerProfile.toString())
        }
    }
}