package com.example.codev

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.codev.addpage.AddNewProjectActivity
import com.example.codev.addpage.AddNewStudyActivity
import com.example.codev.addpage.EditProject
import com.example.codev.addpage.EditStudy
import com.example.codev.databinding.ActivityRecruitApplyListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitApplyListActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitApplyListBinding
    private lateinit var adapter1: AdapterRecruitApplicants1
    private lateinit var adapter2: AdapterRecruitApplicants2

    //private lateinit var studyData: EditStudy
//    private lateinit var projectData: ApplicantData
//    private var partList: ArrayList<ApplicantData> = arrayListOf()
    private var id: Int = -1
    private var type: String = "" //초기는 TEMP
    private var limit: Int = -1
    private var peopleNum: Int = -1
    private var part: String = ""


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


        loadData(this, type , id, "TEMP") //처음에 기본으로 현재 선택된 지원자 보이게


    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
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

        id = intent.getIntExtra("id",-1) //co_projectId로 전달해야함
        type = intent.getStringExtra("type").toString() //PROJECT or STUDY
        limit = intent.getIntExtra("limit",-1)


        loadData(this, type , id, "TEMP")

        //if TEMP의 데이터가 1개 이상이면 btn_reset, btn_done_recruit 활성화
//        if(viewBinding.bottomBtn.isEnabled){
//            viewBinding.btnReset.textCo
//        }
        //여기

        //선택하기 버튼
        viewBinding.btnEdit.setOnClickListener {
            if (viewBinding.btnEdit.isChecked){
                viewBinding.btnEdit.text = "취소하기"
            }else{
                viewBinding.btnEdit.text = "선택하기"
            }
            Log.d("test",part)
            if (part == "TEMP"){
                viewBinding.bottomBtn.isGone = viewBinding.btnEdit.isChecked
                viewBinding.btnSelect1.isGone = !viewBinding.btnEdit.isChecked
            }else{
                viewBinding.btnSelect2.isGone = !viewBinding.btnEdit.isChecked
            }
            //토글헤더에서 눌렸을 때 vs 토글아이템에서 눌렸을 때 나눠야 함
            adapter2.setEdit(viewBinding.btnEdit.isChecked)
            adapter2.setCount(0)
            adapter2.resetSelectList()
            adapter2.resetIsChecked()
            adapter2.notifyDataSetChanged()
            enableDelete(false)
            enableRegist(false)
        }


        //btnSelect1 : 토글헤더 선택된 상태에서 btnChoose 눌렀을 때만 보이게 (지원자 선택취소 버튼)
        //btnSelect2 : 토글아이템 선택된 상태에서 btnChhose 눌렀을 때만 보이게 (지원자 선택 버튼)
        //bottomBtn : 토글헤더 눌려있을 때만 보이게 (모집완료/초기화 버튼)

        //isChecked 된 항목들 편집기능
        viewBinding.btnSelect1.setOnClickListener {
            Log.d("test", "삭제")
            Log.d("test",adapter2.getIsChecked().toString())
            Log.d("test",adapter2.getSelectList().size.toString())
            for (i:Int in 0 until adapter2.getSelectList().size){
                Log.d("test: 지원자 삭제 목록",adapter2.getSelectList()[i].toString())
                //editApplicant(this,adapter2.getSelectList()[i])
            }
            editApplicant(this, type, id, adapter2.getSelectList())
            loadData(this, type, id, part)

            viewBinding.btnEdit.isChecked = !viewBinding.btnEdit.isChecked
            viewBinding.btnSelect1.isGone = !viewBinding.btnEdit.isChecked
            viewBinding.btnEdit.text = "선택하기"
            adapter2.setEdit(viewBinding.btnEdit.isChecked)
            adapter2.setCount(0)
            adapter2.resetSelectList()
            adapter2.resetIsChecked()
            adapter2.notifyDataSetChanged()
            enableDelete(false)
            enableRegist(false)
        }
        viewBinding.btnSelect2.setOnClickListener {
            Log.d("test", "선택")
            Log.d("test",adapter2.getIsChecked().toString())
            Log.d("test",adapter2.getSelectList().size.toString())
            for (i:Int in 0 until adapter2.getSelectList().size){
                Log.d("test: 지원자 선택 목록",adapter2.getSelectList()[i].toString())
//                editApplicant(this,adapter2.getSelectList()[i])
            }
            editApplicant(this, type, id, adapter2.getSelectList())
            loadData(this, type, id, part)

            viewBinding.btnEdit.isChecked = !viewBinding.btnEdit.isChecked
            viewBinding.btnSelect2.isGone = !viewBinding.btnEdit.isChecked
            viewBinding.btnEdit.text = "선택하기"
            adapter2.setEdit(viewBinding.btnEdit.isChecked)
            adapter2.setCount(0)
            adapter2.resetSelectList()
            adapter2.resetIsChecked()
            adapter2.notifyDataSetChanged()
            enableDelete(false)
            enableRegist(false)
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

    private fun enableDelete(boolean: Boolean){
        if (boolean != viewBinding.btnSelect1.isEnabled){
            viewBinding.btnSelect1.isEnabled = boolean
            viewBinding.btnSelect1.isSelected = boolean
        }
    }

    private fun enableRegist(boolean: Boolean){
        if (boolean != viewBinding.btnSelect2.isEnabled){
            viewBinding.btnSelect2.isEnabled = boolean
            viewBinding.btnSelect2.isSelected = boolean
        }
    }

    private fun enableResetOrDone(boolean: Boolean){ //true가 넘어왔다 일단
        if(boolean == viewBinding.bottomBtn.isEnabled){
            viewBinding.btnReset.isEnabled = boolean
            viewBinding.btnDoneRecruit.isEnabled = boolean
            viewBinding.btnReset.isSelected = boolean
            viewBinding.btnDoneRecruit.isSelected = boolean
            viewBinding.bottomBtn.isEnabled = !boolean
            viewBinding.btnReset.setTextColor(ContextCompat.getColor(this!!,R.color.black_700))
            viewBinding.btnDoneRecruit.setTextColor(ContextCompat.getColor(this!!,R.color.white))
        }
    }

//    Log.d("test","리콜받음 $it")
//    loadData(context, type, id, it)
//    viewBinding.btnEdit.isChecked = false
//    viewBinding.btnEdit.text = "선택하기"

    private fun setAdapter1(dataList: ArrayList<ApplicantData>, context: Context, limit: Int){
        adapter1 = AdapterRecruitApplicants1(context, dataList, limit){
            Log.d("test","리콜받음 $it")
            loadData(context, type, id, it)
            viewBinding.btnEdit.isChecked = false
            viewBinding.btnEdit.text = "선택하기"
        }
        val currentPosition = (viewBinding.part.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        viewBinding.part.adapter = adapter1
        viewBinding.part.scrollToPosition(currentPosition)
    }

    private fun setAdapter2(dataList: ArrayList<ApplicantInfoData>, context: Context){
        adapter2 = AdapterRecruitApplicants2(context, dataList){
            Log.d("test","리콜받음")
            if (it == 1){
                enableDelete(true)
                enableRegist(true)
            }else if (it == 0){
                enableDelete(false)
                enableRegist(false)
            }
        }
        viewBinding.applyList.adapter = adapter2
    }

    private fun loadData(context: Context, type:String, id: Int, coPart: String){
        if(type == "PROJECT") {
            RetrofitClient.service.getApplyerProjectList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, coPart).enqueue(object : Callback<ResApplyerList> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResApplyerList>, response: Response<ResApplyerList>) {
                    if (response.isSuccessful.not()) {
                        Log.d("test: 조회실패", response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        when (response.code()) {
                            200 -> {
                                response.body()?.let {
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.toString()}")
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.result.message.co_appllicantsInfo}")
                                    setAdapter1(it.result.message.co_applicantsCount, context, limit)
                                    setAdapter2(it.result.message.co_appllicantsInfo, context)
                                    part = it.result.message.co_part
                                    if (part == "TEMP"){
                                        viewBinding.bottomBtn.isGone = false
                                        viewBinding.btnSelect1.isGone = true
                                        viewBinding.btnSelect2.isGone = true
                                        peopleNum = it.result.message.co_tempSavedApplicantsCount
                                        viewBinding.applicantNum.text = "현재 선택한 지원자 $peopleNum"
                                        if(peopleNum != 0){ //초기화, 모집완료 활성화
//                                            viewBinding.btnReset.isEnabled = true
//                                            viewBinding.btnDoneRecruit.isEnabled = true
                                            enableResetOrDone(true)
                                            Log.d("enableResetOrDone 되나? ", "chk")
                                        }else{
                                            enableResetOrDone(false)
                                            viewBinding.btnReset.setTextColor(ContextCompat.getColor(context!!,R.color.black_300))
                                            viewBinding.btnDoneRecruit.setTextColor(ContextCompat.getColor(context!!,R.color.black_500))
                                        }
                                    }else{
                                        viewBinding.bottomBtn.isGone = true
                                        viewBinding.btnSelect1.isGone = true
                                        viewBinding.btnSelect2.isGone = true
                                        peopleNum = it.result.message.co_appllicantsInfo.size
                                        viewBinding.applicantNum.text = "현재 파트 지원자 $peopleNum"
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResApplyerList>, t: Throwable) {
                    Log.d("test: 조회실패 - loadData(플젝 지원자 전체조회): ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else if(type == "STUDY"){
            RetrofitClient.service.getApplyerStudyList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, coPart).enqueue(object : Callback<ResApplyerList> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResApplyerList>, response: Response<ResApplyerList>) {
                    if (response.isSuccessful.not()) {
                        Log.d("test: 조회실패", response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        when (response.code()) {
                            200 -> {
                                response.body()?.let {
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.toString()}")
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.result.message.co_appllicantsInfo}")
                                    setAdapter1(it.result.message.co_applicantsCount, context, limit)
                                    setAdapter2(it.result.message.co_appllicantsInfo, context)
                                    part = it.result.message.co_part
                                    if (part == "TEMP"){
                                        viewBinding.bottomBtn.isGone = false
                                        viewBinding.btnSelect1.isGone = true
                                        viewBinding.btnSelect2.isGone = true
                                        peopleNum = it.result.message.co_tempSavedApplicantsCount
                                        viewBinding.applicantNum.text = "현재 선택한 지원자 $peopleNum"
                                    }else{
                                        viewBinding.bottomBtn.isGone = true
                                        viewBinding.btnSelect1.isGone = true
                                        viewBinding.btnSelect2.isGone = true
                                        peopleNum = it.result.message.co_appllicantsInfo.size
                                        viewBinding.applicantNum.text = "현재 파트 지원자 $peopleNum"
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResApplyerList>, t: Throwable) {
                    Log.d("test: 조회실패 - loadData(스터디 지원자 전체조회): ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    //지원자 EDIT
    private fun editApplicant(context: Context, type: String, id: Int, editList: List<String>){
        if (type == "PROJECT"){
            RetrofitClient.service.requestProjectApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqUpdateApplicant(editList)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 지원자 편집 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 지원자 편집 성공", "\n${it.toString()}")

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }
        else if(type == "STUDY"){
            RetrofitClient.service.requestStudyApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqUpdateApplicant(editList)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 지원자 편집  실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 지원자 편집  성공", "\n${it.toString()}")

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }
    }

}
