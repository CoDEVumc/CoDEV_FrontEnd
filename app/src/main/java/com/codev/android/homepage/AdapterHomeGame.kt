package com.codev.android.homepage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codev.android.AdapterRecruitProjectList
import com.codev.android.PData
import com.codev.android.R
import com.codev.android.databinding.HomeHotGameBinding

class AdapterHomeGame(private val context: Context, private val listData: ArrayList<HomeGameItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class GameItemViewHolder(val context: Context, private val viewBinding: HomeHotGameBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(data: HomeGameItem, position: Int){
            Glide.with(context).load(data.imageUrl).error(R.drawable.test_error_image).into(viewBinding.hotPostImage)
            viewBinding.postTitle.text = data.title
            viewBinding.companyName.text = data.company
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GameItemViewHolder(context, HomeHotGameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is GameItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    override fun getItemCount(): Int = listData.size
}