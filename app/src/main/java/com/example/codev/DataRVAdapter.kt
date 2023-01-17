package com.example.codev

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.databinding.ProjectListItemBinding
import kotlinx.android.synthetic.main.studyList_item.view.*

//import com.example.codev.databinding.ItemDataBinding

class DataRVAdapter( private val dataList: ArrayList<ProjectData> ): RecyclerView.Adapter<DataRVAdapter.DataViewHolder>() {

    //viewHolder 객체
    inner class DataViewHolder(private val viewBinding: ProjectListItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(data: ProjectData){ //이렇게 해야 viewBinding의 데이터 값을 가져올 수 있음
            viewBinding.pTitle.text = data.ptitle
            viewBinding.pDday.text = data.pdday
            viewBinding.pHeart.boolean = data.pheart
            viewBinding.pNum.int = data.pnum
            viewBinding.imageView.text = data.pimages

            viewBinding.pContent.text = data.pcontent
        }
        init{ //item 누르면 상세 페이지로 이동 <-- 나중에 상세 페이지 짤 때 활용!!
            viewBinding.projectItem.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                Log.d("클릭","$position: 클릭했습니다. ")
                dataList.removeAt(position)
                notifyItemRemoved(position)

                return@OnClickListener //여기에 true 입력 해야 되는거 같은디
            })
        }
    }

    //ViewHolder 만들어질 때 실행할 동작
    //MainActivity에서 하던 viewBinding이랑 똑같은 작업
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        var viewBinding = ProjectListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(viewBinding)
    }

    //ViewHolder가 실제로 데이터를 표시해야할 때 호출되어야 하는 함수
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    //표현할 Item의 총 갯수
    override fun getItemCount(): Int = dataList.size

    //ClickListener
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}

//Data 객체 생성 --> Data 표시할 xml 만들고 --> DataRVAdapter 생성 --> viewHolder 객체 만들고
//ㄴProjectData --> projectList.xml --> DataRVAdapter -->