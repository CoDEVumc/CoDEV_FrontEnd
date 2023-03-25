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

import com.example.codev.databinding.RecycleCommunityQuestionAndInfoListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterCommunityQuestionList(private val context: Context, private val listData: ArrayList<QIData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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
        fun bind(data: QIData, position: Int){
            //작성자 프로필 사진
            //binding.image.text = data.profileImg
            //작성자 닉네임
            binding.writer.text = data.co_email //이거 이메일이 아니라 닉네임으로 api 수정 요청 해야돼 수진아

            //작성 일자
            binding.writenDate.text = data.createdAt

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

            //질문글 이미지
            //binding.img.text = data.co_mainImg



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

}