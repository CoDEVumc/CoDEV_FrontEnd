package com.codev.android.addpage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codev.android.R
import com.codev.android.databinding.ListItemBinding

class Stack2RVAdapter(private var itemList: ArrayList<AddListItem>, private val returnData: (AddListItem) -> Unit): RecyclerView.Adapter<Stack2RVAdapter.CallbackViewHolder>(){

    inner class CallbackViewHolder(private val viewBinding: ListItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(item:AddListItem){

            if(item.isSelected){
                viewBinding.checkIcon.setImageResource(R.drawable.select_yes_icon)
            }else{
                viewBinding.checkIcon.setImageResource(R.drawable.select_no_icon)
            }

            viewBinding.stackName.text = item.name
            viewBinding.root.setOnClickListener {
                returnData(item)
                item.isSelected = !item.isSelected
                notifyItemChanged(adapterPosition)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallbackViewHolder {
        val viewBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallbackViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CallbackViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}