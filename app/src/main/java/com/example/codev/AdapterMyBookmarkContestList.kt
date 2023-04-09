package com.example.codev

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.codev.databinding.RecycleCommunityContestListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterMyBookmarkContestList(private val context: Context, private val listData: ArrayList<BookmarkCData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ContestViewHolder(context, RecycleCommunityContestListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ContestViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class ContestViewHolder(val context: Context, private val binding: RecycleCommunityContestListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: BookmarkCData, position: Int){
            //디데이
            //binding.dday.text = data.???

            //타입 (ex. 기획/아이디어)
            //binding.type.text = data.??

            //공모전 제목
            //binding.title.text = data.??

            //주최 기관명
            //binding.content.text = data.???

            //북마크 수
            //binding.bnum.text = data.??.toString()

            //대표사진
//            Glide.with(context)
//                .load(data.???)
//                .into(binding.contestImg)



            binding.item.setOnClickListener { //상세조회 연결
//                val intent = Intent(binding.item.context, RecruitDetailActivity::class.java)
//                intent.putExtra("id",data.co_projectId)
//                intent.putExtra("type","PROJECT")
//                intent.putExtra("dday",binding.dday.text)
//                Log.d("test : 선택한 질문글 아이디", data.co_qnaId.toString())
//                startActivity(binding.item.context,intent,null)
            }
        }
    }

    fun changeToDateForm(date: String): String{
        //"2023-03-11T10:16:42.000+00:00" <- 이렇게 나오는 거
        ////"2023-03-11 T 10:16:42.000+00:00" <- 이렇게 나오는 거
        //23/03/11 10:16
        val finWritenDate: String //최종 결과

        val arr = date.split("T") // ["2023-03-11","10:16:42.000+00:00"]
        val cday: String = arr[0] //"2023-03-11"
        val ctime: String = arr[1] //"10:16:42.000+00:00"
        val cdarray = cday.split("-") // ["2023","03","11"]
        val ctarray = ctime.split(":",".") // ["10","16","42.000+00:00"]

        val createdY: String = cdarray[0].slice(2..3) //23
        val createdM: String = cdarray[1] //03
        val createdD: String = cdarray[2] //11

        val createdHour: String = ctarray[0] //10
        val createdMin: String = ctarray[1] //16

        finWritenDate = createdY+"/"+createdM+"/"+createdD+" "+createdHour+":"+createdMin

        return finWritenDate
    }

}