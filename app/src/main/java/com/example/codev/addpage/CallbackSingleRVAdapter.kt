package com.example.codev.addpage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.R
import com.example.codev.databinding.ListItemBinding

class CallbackSingleRVAdapter(private var itemList: ArrayList<AddListItem>, private var selectedIdx: Int, private val returnData: (Int) -> Unit): RecyclerView.Adapter<CallbackSingleRVAdapter.CallbackViewHolder>(){

    inner class CallbackViewHolder(private val viewBinding: ListItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(item:AddListItem){

            if(item.isSelected){
                viewBinding.checkIcon.setImageResource(R.drawable.select_yes_icon)
            }else{
                viewBinding.checkIcon.setImageResource(R.drawable.select_no_icon)
            }

            viewBinding.stackName.text = item.name
            viewBinding.root.setOnClickListener {
                Log.d("debugIndex", "first: selectIdx: $selectedIdx")
                if(selectedIdx != -1 && selectedIdx != adapterPosition){ //이미 클릭한 다른 항목을 선택 해제하기
                    itemList[selectedIdx].isSelected = false
                    notifyItemChanged(selectedIdx)
                }
                selectedIdx = adapterPosition
                Log.d("debugIndex", "end: selectIdx: $selectedIdx")
                item.isSelected = true
                notifyItemChanged(adapterPosition)

                returnData(adapterPosition)
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