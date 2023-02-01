package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityRecruitDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitDetailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitDetailBinding

    private var id: Int = -1
    private var type: String = ""
    private var dday: String = ""

    override fun onResume() {
        super.onResume()
        id = intent.getIntExtra("id",-1)
        type = intent.getStringExtra("type").toString()
        dday = intent.getStringExtra("dday").toString()
        Log.d("test: 상세페이지 넘어온 type, id", "$type : $id")
        if (id != -1 && type != null && dday != null) {
            loadRecruitDetail(this,type,id,dday)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRecruit.toolbar3.title = ""
        //모집글 작성자일시 메뉴 추가(수정, 삭제) 필요
        //스크롤 위치가 이미지 indicator 정도 오면 툴바 배경 흰색으로 변경 필요

        setSupportActionBar(viewBinding.toolbarRecruit.toolbar3)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadRecruitDetail(context:Context, type:String, int: Int, dday: String){
        if (type == "PROJECT"){
            RetrofitClient.service.getProjectDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), int).enqueue(object: Callback<ResGetRecruitDetail> {
                override fun onResponse(call: Call<ResGetRecruitDetail>, response: Response<ResGetRecruitDetail>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 조회실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: 모집글 조회 성공! ", "\n${it.toString()}")
                                    Log.d("test: projectId",it.result.Complete.co_projectId.toString())
                                    Log.d("test: studyId",it.result.Complete.co_studyId.toString())
                                    setStackAdapter(context, it.result.Complete.co_languageList)
                                    setPartAdapter(context,it.result.Complete.co_partList)
                                    setImageAdapter(context,it.result.Complete.co_photos)
                                    viewBinding.type.text = "프로젝트"
                                    viewBinding.title.text = it.result.Complete.co_title
                                    viewBinding.location.text = it.result.Complete.co_location
                                    viewBinding.day.text = dday
                                    viewBinding.text.text = it.result.Complete.co_content
                                    //co_total 적용 x, co_nickname(작성자) 없음, co_heart 적용x
                                    viewBinding.total.text = it.result.Complete.co_total.toString() + "명 모집중"
                                    viewBinding.heartCount.text = it.result.Complete.co_heartCount.toString()
                                    viewBinding.heart.isChecked = it.result.Complete.co_heart
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResGetRecruitDetail>, t: Throwable) {
                    Log.d("test: 모집글 조회실패", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }else if(type == "STUDY"){
            RetrofitClient.service.getStudyDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), int).enqueue(object: Callback<ResGetRecruitDetail> {
                override fun onResponse(call: Call<ResGetRecruitDetail>, response: Response<ResGetRecruitDetail>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 조회실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: 모집글 조회 성공! ", "\n${it.toString()}")
                                    Log.d("test: projectId",it.result.Complete.co_projectId.toString())
                                    Log.d("test: studyId",it.result.Complete.co_studyId.toString())
                                    setStackAdapter(context, it.result.Complete.co_languageList)
                                    setPartAdapter(context,it.result.Complete.co_partList)
                                    setImageAdapter(context,it.result.Complete.co_photos)
                                    viewBinding.type.text = "스터디"
                                    viewBinding.title.text = it.result.Complete.co_title
                                    viewBinding.location.text = it.result.Complete.co_location
                                    viewBinding.day.text = dday
                                    viewBinding.text.text = it.result.Complete.co_content
                                    //co_total 적용 x, co_nickname(작성자) 없음, co_heart 적용x
                                    viewBinding.total.text = it.result.Complete.co_total.toString() + "명 모집중"
                                    viewBinding.heartCount.text = it.result.Complete.co_heartCount.toString()
                                    viewBinding.heart.isChecked = it.result.Complete.co_heart
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResGetRecruitDetail>, t: Throwable) {
                    Log.d("test: 모집글 조회실패", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setPartAdapter(context: Context, partList: ArrayList<RecruitPartLimit>){
        if(!partList.isNullOrEmpty()){
            viewBinding.part.adapter = AdapterRecruitDetailPart(context, partList)
        }
    }

    private fun setStackAdapter(context:Context ,languageList: ArrayList<RecruitLanguage>){
        if (!languageList.isNullOrEmpty()){
            viewBinding.stack.adapter = AdapterRecruitDetailStack(context,languageList)
        }
    }

    private fun setImageAdapter(context: Context, imgList: ArrayList<RecruitPhoto>){
        if(!imgList.isNullOrEmpty()){
            viewBinding.vpImage.adapter = AdapterRecruitDetailImg(context, imgList)
            viewBinding.indicator.attachTo(viewBinding.vpImage)
        }
    }
}