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
import com.example.codev.databinding.RecycleCommunityContestListBinding

import com.example.codev.databinding.RecycleRecruitListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterCommunityContestList(private val context: Context, private val listData: ArrayList<CData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ContestItemViewHolder(context, RecycleCommunityContestListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ContestItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class ContestItemViewHolder(val context: Context, private val binding: RecycleCommunityContestListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: CData, position: Int){
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
//            if(!data.co_mainImg.isNullOrBlank()){
//                binding.img.visibility = View.VISIBLE
//                Glide.with(context)
//                    .load(data.co_mainImg)
//                    .into(binding.img)
//            }

            /*binding.item.setOnClickListener { //상세조회 연결
                val intent = Intent(binding.item.context, InfoDetailActivity::class.java)
                intent.putExtra("id", data.co_infoId)
                Log.d("test : 선택한 정보글 아이디", data.co_infoId.toString())
                startActivity(binding.item.context,intent,null)
            }*/

        }
    }


    private fun request(co_projectId: Int){
        RetrofitClient.service.requestProjectBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()),co_projectId).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: AdapterP__request() 성공! ", "\n${it.toString()}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: AdapterP__request()실패 : ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}