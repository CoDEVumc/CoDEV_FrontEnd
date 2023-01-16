package com.example.codev

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.ListItemBinding

class stack2Adapter(private var context: Context, private var itemList: ArrayList<addListItem>):  RecyclerView.Adapter<stack2Adapter.stack2ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(v: View?, item: addListItem, pos: Int)
    }
    // 리스너 객체 참조를 저장하는 변수
    private var mListener: OnItemClickListener? = null
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }


    inner class stack2ViewHolder(private val viewBinding: ListItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(item: addListItem){
            if(item.isSelected){
                viewBinding.checkIcon.setImageResource(R.drawable.select_yes_icon)
            }else{
                viewBinding.checkIcon.setImageResource(R.drawable.select_no_icon)
            }
            viewBinding.stackName.text = item.name

            viewBinding.root.setOnClickListener {
                val pos = adapterPosition
                mListener?.onItemClick(itemView,item,pos)
                item.isSelected = !item.isSelected
                if(item.isSelected){
                    viewBinding.checkIcon.setImageResource(R.drawable.select_yes_icon)
                }else{
                    viewBinding.checkIcon.setImageResource(R.drawable.select_no_icon)
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): stack2ViewHolder {
        val viewBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return stack2ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: stack2ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}