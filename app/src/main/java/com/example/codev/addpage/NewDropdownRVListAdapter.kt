package com.example.codev.addpage;

import android.content.Context;
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.R
import com.example.codev.databinding.ListItemBinding

import java.util.ArrayList;

class NewDropdownRVListAdapter(private var itemList: ArrayList<AddListItem>): RecyclerView.Adapter<NewDropdownRVListAdapter.NewStackViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(v: View?, item: AddListItem, pos: Int)
    }
    // 리스너 객체 참조를 저장하는 변수
    private var mListener: OnItemClickListener? = null
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    inner class NewStackViewHolder(private val viewBinding: ListItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(item: AddListItem){
            if(item.isSelected){
                viewBinding.checkIcon.setImageResource(R.drawable.select_yes_icon)
            }else{
                viewBinding.checkIcon.setImageResource(R.drawable.select_no_icon)
            }
            viewBinding.stackName.text = item.name
            viewBinding.root.setOnClickListener {
                val pos = adapterPosition
                mListener?.onItemClick(itemView,item,pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewStackViewHolder {
        val viewBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewStackViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: NewStackViewHolder, position: Int) {
        Log.d("list", "nowList: " + itemList.toString())
        Log.d("list", "nowPosition: " + position)
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}
