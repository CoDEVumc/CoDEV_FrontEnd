package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.codev.addpage.EditProject
import com.example.codev.addpage.EditStudy
import com.example.codev.databinding.ActivityRecruitApplyListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitApplyListActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitApplyListBinding
    private lateinit var mainAppActivity: Context
    private lateinit var adapter2: AdapterRecruitApplicants2

    //private lateinit var studyData: EditStudy
    private lateinit var projectData: ApplicantData
    private var partList: ArrayList<ApplicantData> = arrayListOf()
    private var id: Int = -1
    private var type: String = "" //초기는 TEMP


    //private var coPart: String=""//백엔드, 프론트엔드, 기획, 디자인, 기타, TEMP(임시저장)
    //토글에서 선택한게 part에 저장되어야 함

    override fun onResume() {
        super.onResume()
        //툴바 설정
        viewBinding.toolbarApply.toolbar2.title = ""
        viewBinding.toolbarApply.toolbarText.text = "지원현황 보기"
        setSupportActionBar(viewBinding.toolbarApply.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        id = intent.getIntExtra("id",-1) //co_projectId로 전달해야함
        type = intent.getStringExtra("type").toString() //PROJECT or STUDY
        when(type){
            "PROJECT" -> {
                Log.d("test: 상세페이지 넘어온 part, id", "$type : $id")
                if (id != -1) {
                    loadPData(this,type , id, "TEMP") //(처음엔 기본)누른 토글의 part넘겨줘야됨
                }
            }
            "STUDY" -> {

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitApplyListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarApply.toolbar2.title = ""
        setSupportActionBar(viewBinding.toolbarApply.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        loadPData(this, type, id, viewBinding.header1.toString())//아이템 toString 이게 맞아?


        //취소하기 버튼은 안보였다가 선택하기 버튼이 눌리면 나타나야함
        viewBinding.btnCancel.isGone = true //취소하기 버튼 안보이게!

        //선택하기 버튼
        viewBinding.btnChoose.setOnClickListener {
            viewBinding.btnCancel.isGone = !viewBinding.btnChoose.isChecked

            //토글헤더에서 눌렸을 때 vs 토글아이템에서 눌렸을 때 나눠야 함
            adapter2.setEdit(viewBinding.btnChoose.isChecked)
            adapter2.setDeleteCount(0)
            adapter2.resetIsChecked()
            adapter2.resetDeleteList()
            adapter2.notifyDataSetChanged()
            enableDelete(false)

            enableRegist(false)
        }

        //btnSelect1 : 토글헤더 선택된 상태에서 btnChoose 눌렀을 때만 보이게 (지원자 선택취소 버튼)
        //btnSelect2 : 토글아이템 선택된 상태에서 btnChhose 눌렀을 때만 보이게 (지원자 선택 버튼)
        //bottomBtn : 토글헤더 눌려있을 때만 보이게 (모집완료/초기화 버튼)

        //isChecked 된 항목들 삭제기능
        viewBinding.btnSelect1.setOnClickListener {
            Log.d("test", "삭제")
            Log.d("test",adapter2.getIsChecked().toString())
            Log.d("test",adapter2.getDeleteList().size.toString())
            for (i:Int in 0 until adapter2.getDeleteList().size){
                Log.d("test: 삭제 목록",adapter2.getDeleteList()[i].toString())
                deletePortfolio(this,adapter2.getDeleteList()[i])
            }
            viewBinding.btnChoose.isChecked = !viewBinding.btnChoose.isChecked
            viewBinding.btnSelect1.isGone = !viewBinding.btnChoose.isChecked
        }

        //isChecked 된 항목들 선택기능
        viewBinding.btnSelect2.setOnClickListener {
            Log.d("test:","지원자 선택")
            Log.d("test",adapter2.getIsChecked().toString())
            for(i:Int in 0 until adapter2.getRegistList().size){
                Log.d("test: 지원자 선택 목록", adapter2.getRegistList()[i].toString())
                registPortfolio(this, adapter2.getRegistList()[i])
            }
            viewBinding.btnChoose.isChecked = !viewBinding.btnChoose.isChecked
            viewBinding.btnSelect2.isGone = !viewBinding.btnChoose.isChecked
        }


    }

    private fun enableDelete(boolean: Boolean){
        if (boolean != viewBinding.btnSelect1.isEnabled){
            viewBinding.btnSelect1.isEnabled = boolean
            viewBinding.btnCancel.isVisible = true //취소하기 보이게
            viewBinding.btnChoose.isGone = true //선택하기 안보이게
//            if(boolean){
//                //활성화
//                viewBinding.btnDeleteText.setTextColor(getColor(R.color.black_700))
//            }else{
//                //비활성화
//                viewBinding.btnDeleteText.setTextColor(getColor(R.color.black_300))
//            }
        }
    }
    private fun enableRegist(boolean: Boolean){
        if (boolean != viewBinding.btnSelect2.isEnabled){
            viewBinding.btnSelect2.isEnabled = boolean
            viewBinding.btnCancel.isVisible = true //취소하기 보이게
            viewBinding.btnChoose.isGone = true //선택하기 안보이게
//            if(boolean){
//                //활성화
//                viewBinding.btnDeleteText.setTextColor(getColor(R.color.black_700))
//            }else{
//                //비활성화
//                viewBinding.btnDeleteText.setTextColor(getColor(R.color.black_300))
//            }
        }
    }

    private fun setAdapter3(dataList: ArrayList<ApplicantInfoData>, context: Context){
        adapter2 = AdapterRecruitApplicants2(context, dataList, ){
            Log.d("test","리콜받음")
            if (it == 1){
                enableDelete(true)
            }else if (it == 0){
                enableDelete(false)
            }
        }
        viewBinding.applyList.adapter = adapter2
    }
    private fun setAdapter4(dataList: ArrayList<ApplicantInfoData>, context: Context){
        adapter2 = AdapterRecruitApplicants2(context, dataList){
            Log.d("test","리콜받음")
            if (it == 1){
                enableRegist(true)
            }else if (it == 0){
                enableRegist(false)
            }
        }
        viewBinding.applyList.adapter = adapter2
    }

    //loadPData / loadSData 둘로 나누기
    private fun loadData(context: Context, id: Int){

    }
    private fun loadPData(context: Context, type:String, id: Int, coPart: String){
        if(type == "PROJECT") {
            RetrofitClient.service.getApplyerProjectList(
                AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),
                id, coPart
            ).enqueue(object : Callback<ResApplyerList> {
                override fun onResponse(
                    call: Call<ResApplyerList>,
                    response: Response<ResApplyerList>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d("test: 조회실패", response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        when (response.code()) {
                            200 -> {
                                response.body()?.let {
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.toString()}")
                                    setAdapter3(it.result.message.co_appllicantsInfo, context)
                                    setAdapter4(it.result.message.co_appllicantsInfo, context)
                                }

                            }
                        }
                    }
                }


                override fun onFailure(call: Call<ResApplyerList>, t: Throwable) {
                    Log.d("test: 조회실패 - RPF > loadPData(플젝 지원자 전체조회): ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else if(type == "STUDY"){

        }
    }
//    private fun loadSData(context: Context, coPart: String){
//        RetrofitClient.service.getApplyerStudyList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(mainAppActivity)),
//            id, coPart).enqueue(object: Callback<ResApplyerList>{
//            override fun onResponse(call: Call<ResApplyerList>, response: Response<ResApplyerList>) {
//                if(response.isSuccessful.not()){
//                    Log.d("test: 조회실패",response.toString())
//                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }else{
//                    when(response.code()){
//                        200->{
//                            response.body()?.let {
//                                Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.toString()}")
//                                setAdapter3(it.result.message.co_appllicantsInfo, context)
//                                setAdapter4(it.result.message.co_appllicantsInfo, context)
//                            }
//
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ResApplyerList>, t: Throwable) {
//                Log.d("test: 조회실패 - RPF > loadData_p(플젝 전체조회): ", "[Fail]${t.toString()}")
//                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }


    //지원자 선택취소
    private fun deletePortfolio(context: Context,coPortfolioId: Int){
        RetrofitClient.service.deletePortFolio(coPortfolioId,AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object:
            Callback<ResDeletePortfolio> {
            override fun onResponse(call: Call<ResDeletePortfolio>, response: Response<ResDeletePortfolio>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                            loadData(context,coPortfolioId)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResDeletePortfolio>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    //지원자 선택
    private fun registPortfolio(context: Context,coPortfolioId: Int){
        RetrofitClient.service.deletePortFolio(coPortfolioId,AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object:
            Callback<ResDeletePortfolio> {
            override fun onResponse(call: Call<ResDeletePortfolio>, response: Response<ResDeletePortfolio>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                            loadData(context,coPortfolioId)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResDeletePortfolio>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

}
