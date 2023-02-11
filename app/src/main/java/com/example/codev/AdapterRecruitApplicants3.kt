package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
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
import java.util.*
import kotlin.collections.ArrayList

//[바디_선택하기 누름] 토글헤더 클릭했을 때 -> 지원자 선택취소
//private val context: Context 요거 일단 뺌
class AdapterRecruitApplicants3(private val context: Context, private val listData: ArrayList<ApplicantInfoData>, private val returnDeleteCount: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var EDIT: Boolean = false
    private var DELETECOUNT: Int = 0
    private var isChecked: Array<Boolean> = Array(listData.size){false}
    private var deleteList: ArrayList<Int> = arrayListOf()

    fun setEdit(boolean: Boolean){
        EDIT = boolean
    }

    fun setDeleteCount(int: Int){
        DELETECOUNT = int
    }

    fun resetIsChecked(){
        Arrays.setAll(isChecked){false}
    }

    fun getIsChecked(): Array<Boolean>{
        return isChecked
    }

    fun resetDeleteList(){
        deleteList.clear()
    }

    fun getDeleteList(): ArrayList<Int>{
        return deleteList
    }

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ApplicantItemViewHolder(RecycleApplicantItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ApplicantItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class ApplicantItemViewHolder(private val binding: RecycleApplicantItem2Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ApplicantInfoData, position: Int){
            //binding.imgSelected.setImageURI(data.profileImg) //지원자 프로필사진
            when(data.co_temporaryStorage){ //선택여부
                true -> binding.imgSelected.isGone = false //선택된 지원자면 imgSelected.isGone = false
                false -> binding.imgSelected.isGone = true //선택안된 지원자면 imgSelected.isGone = true
            }

            binding.applyerPortfolioTitle.text = data.co_title
            binding.applyerName.text = data.co_name
            binding.applyerPart.text = data.co_part

            binding.chkChoose.isChecked = isChecked[position]
            binding.portfolio.isSelected = isChecked[position]
            changeEditMode(EDIT)

            binding.chkChoose.setOnClickListener {
                isChecked[position] = binding.chkChoose.isChecked
                if(binding.chkChoose.isChecked){
                    deleteList.add(data.co_portfolioId)
                }else{
                    deleteList.remove(data.co_portfolioId)
                }
                deleteCounter()
                notifyItemChanged(position)
            }

            binding.portfolio.setOnClickListener {
                Log.d("test", "포트폴리오 클릭 $position")
                if (EDIT){
                    binding.chkChoose.isChecked = !binding.chkChoose.isChecked
                    isChecked[position] = binding.chkChoose.isChecked
                    if(binding.chkChoose.isChecked){
                        deleteList.add(data.co_portfolioId)
                    }else{
                        deleteList.remove(data.co_portfolioId)
                    }
                    deleteCounter()
                    notifyItemChanged(position)
                }else{
                    //포트폴리오 세부 페이지 이동
                    val intent = Intent(binding.portfolio.context, PfDetailActivity::class.java)
                    intent.putExtra("id", data.co_portfolioId.toString())
                    Log.d("test",data.co_portfolioId.toString())
                    ContextCompat.startActivity(binding.portfolio.context, intent, null)
                }
            }


//            binding.portfolioTitle.text = data.co_title
//            binding.portfolioUpdatedAt.text = convertTimestampToDate(data.updatedAt)
//            binding.portfolio.setOnClickListener {
//                Log.d("test", "포트폴리오 클릭 $position")
//                val intent = Intent(binding.portfolio.context, PfDetailActivity::class.java)
//                intent.putExtra("id", data.co_portfolioId.toString())
//                Log.d("test",data.co_portfolioId.toString())
//                startActivity(binding.portfolio.context, intent, null)
//            }

        }

        private fun changeEditMode(boolean: Boolean){
            binding.chkChoose.isGone = !boolean
            binding.temp.isGone = !boolean
        }

        private fun deleteCounter() {
            if (binding.chkChoose.isChecked){
                DELETECOUNT += 1
                Log.d("test", "삭제할 항목수: $DELETECOUNT")
                if (DELETECOUNT == 1){
                    Log.d("test","리콜")
                    returnDeleteCount(DELETECOUNT)
                }
            }else{
                DELETECOUNT -= 1
                Log.d("test", "삭제할 항목수: $DELETECOUNT")
                if (DELETECOUNT == 0){
                    Log.d("test","리콜")
                    returnDeleteCount(DELETECOUNT)
                }
            }
        }


    }

    private fun request(co_projectId: Int){ //프로젝트 지원자 선택 & 선택취소
        //북마크 : co_heart : Boolean <-- true면 채운 하트 / false면 안채운 하트 && 하트 하트 자체는 Selector로 바꾸기
        //            binding.heart.isChecked = listData[position].co_heart
        //            binding.heart.setOnClickListener {
        //                listData[position].co_heart = binding.heart.isChecked
        //                request(data.co_projectId)
        //            }
        RetrofitClient.service.requestProjectApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),co_projectId).enqueue(object:
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: Adapter1 성공! ", "\n${it.toString()}")

                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: Adapter1 실패 : ", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }




}

