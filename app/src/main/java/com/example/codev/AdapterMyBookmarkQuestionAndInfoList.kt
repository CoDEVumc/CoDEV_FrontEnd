package com.example.codev

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.codev.databinding.RecycleCommunityQuestionAndInfoListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterMyBookmarkQuestionAndInfoList(private val context: Context, private val listData: ArrayList<BookmarkQIData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  QuestionItemViewHolder(context, RecycleCommunityQuestionAndInfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is QuestionItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class QuestionItemViewHolder(val context: Context, private val binding: RecycleCommunityQuestionAndInfoListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: BookmarkQIData, position: Int){
            //작성자 프로필 사진
            Glide.with(context)
                .load(data.profileImg)
                .into(binding.image)

            //작성자 닉네임
            binding.writer.text = data.co_nickname

            //작성 일자
            //binding.writenDate.text = data.createdAt
            val date = data.createdAt
            val finWritenDate: String = changeToDateForm(date)
            binding.writenDate.text = finWritenDate

            //질문글 제목
            binding.title.text = data.co_title

            //질문글 내용
            binding.content.text = data.content

            //스마일 수
            binding.snum.text = data.co_likeCount.toString()

            //댓글 수
            binding.cnum.text = data.co_commentCount.toString()

            //북마크 수
            binding.bnum.text = data.co_markCount.toString()

            //정보글, 질문글 이미지
            if(!data.co_mainImg.isNullOrBlank()){
                binding.img.visibility = View.VISIBLE
                Glide.with(context)
                    .load(data.co_mainImg)
                    .into(binding.img)
            }


            //정보글, 질문글 분리 해서 상세조회랑 연결!!!!!!
            binding.item.setOnClickListener { //상세조회 연결
                val intent = Intent(binding.item.context, InfoDetailActivity::class.java)
                if(data.co_infoId!=null) {
                    intent.putExtra("id", data.co_infoId)
                    Log.d("test : 선택한 정보글 아이디", data.co_infoId.toString())
                    startActivity(binding.item.context, intent, null)
                }
                else if(data.co_qnaId!=null){
                    intent.putExtra("id", data.co_qnaId)
                    Log.d("test : 선택한 정보글 아이디", data.co_qnaId.toString())
                    startActivity(binding.item.context, intent, null)
                }
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