package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.codev.addpage.AddPfPageActivity
import com.example.codev.addpage.DefaultPf
import com.example.codev.addpage.PfDetailActivity
import com.example.codev.databinding.*
import com.google.gson.JsonObject
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat

//[토글] : HEADER(상세조회), ITEM(상세조회)
class AdapterRecruitApplicants1(private val context: Context, private val listData: ArrayList<ApplicantData>, private val limit: Int, private val returnData: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER = 0
    private val ITEM = 1

    private val isSelectedList : Array<Boolean> = Array(listData.size + 1){false}
    private var colored: Boolean = false


    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            HEADER ->
                ApplicantHeaderViewHolder(RecycleRecruitApplyPartHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->{
                ApplicantItemViewHolder(RecycleRecruitApplyPartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ApplicantHeaderViewHolder -> {
                holder.bind(0)
            }
            is ApplicantItemViewHolder -> {
                holder.bind(listData[position - 1], position -1)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size + 1

    //뷰 홀더 타입
    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> HEADER
            else -> ITEM
        }
    }

    //Item의 ViewHolder 객체 [토글]
    inner class ApplicantItemViewHolder(private val binding: RecycleRecruitApplyPartItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(data: ApplicantData, position: Int){
            binding.part.text = data.co_part
            binding.num.text = data.co_limit.toString() + "명"

            binding.partToggle.isSelected = isSelectedList[position]

            /*binding.partToggle.setOnClickListener{
                Log.d("AdapterRecruitApplyList: ", "분야 토글 클릭 $adapterPosition")

                colored = true
                returnData(binding.part.text.toString()) //토글아이템 클릭 시 "프론트엔드, 백엔드, 기획, 디자인, 기타" 넘겨줌
            }*/

            binding.partToggle.setOnClickListener {
                //다중선택(position 필요) -> 단일선택()
                isSelectedList[position] = binding.partToggle.isSelected
                binding.partToggle.isSelected = true
                if(binding.partToggle.isSelected){
                    //몇번째 토글이 눌렸는지
                    Log.d("ApaterRecruitApplyList: ","분야 토글 클릭 $adapterPosition")
                }
                returnData(binding.part.text.toString()) //토글아이템 클릭 시 "프론트엔드, 백엔드, 기획, 디자인, 기타" 넘겨줌

                notifyItemChanged(0)
                //notifyItemChanged(position)
            }


            //binding.partToggle.isSelected = colored
        }
    }

    //Header ViewHolder 객체 [토글]
    inner class ApplicantHeaderViewHolder(private val binding: RecycleRecruitApplyPartHeaderBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(position: Int){
            binding.num.text = limit.toString() + "명"

            /*binding.partToggle.setOnClickListener {
                Log.d("ApaterRecruitApplyList: ","분야 토글 클릭 $position")
                returnData("TEMP") //토글헤더 클릭 시 TEMP 넘겨줌

                //클릭된 아이템의 상태 저장
                isSelectedList[0] = true
                colored = true

                binding.root.isSelected = true
                val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.recruit_apply_partbox)
                binding.root.background = backgroundDrawable

                //리사이클러뷰 아이템 변경 알림
                notifyItemChanged(0)
                Log.d("(OnClickListener안)partTogle 의 상태 : ", binding.partToggle.isSelected.toString()) //여기서 true주면 안돼

            }*/
            //retry!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            binding.partToggle.setOnClickListener {
                //다중선택(position 필요) -> 단일선택()
                Log.d("ApaterRecruitApplyList: ","분야 토글 클릭 $position")
                returnData("TEMP") //토글헤더 클릭 시 TEMP 넘겨줌
                binding.partToggle.isSelected = !binding.partToggle.isSelected
                notifyItemChanged(0)
            }

            //왜 이거 두번돌아
            Log.d("(OnClick밖 log)partTogle 의 상태 : ", binding.partToggle.isSelected.toString())

        }
    }

}




//if(binding.partToggle.isSelected){
//    Log.d("partToggle : ", "isSelected. ")
//    binding.partToggle.isSelected = true //토글 배경색 바꿀라고
//    binding.part.isSelected = true //아이콘 색 바꿀라고
//    binding.num.setTextColor(ContextCompat.getColor(context!!,R.color.black_900)) //글씨색 바꿀라고
//}
//else{
//    Log.d("partToggle : ", "notSelected. ")
//    binding.partToggle.isSelected = false
//    binding.part.isSelected = false
//    binding.num.setTextColor(ContextCompat.getColor(context!!,R.color.black_500)) //글씨색 바꿀라고
//}