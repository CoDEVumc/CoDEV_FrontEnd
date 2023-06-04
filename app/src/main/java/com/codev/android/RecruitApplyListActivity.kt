package com.codev.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codev.android.databinding.ActivityRecruitApplyListBinding
import com.codev.android.databinding.RecycleRecruitApplyPartHeaderBinding
import com.codev.android.databinding.RecycleRecruitApplyPartItemBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.isGone as isGone1


class RecruitApplyListActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitApplyListBinding
    private lateinit var adapter1: AdapterRecruitApplicants1
    private lateinit var adapter2: AdapterRecruitApplicants2

    //private lateinit var studyData: EditStudy
    private var loadStatus = false

    private lateinit var projectData: ApplicantData
    private var applicantList: ArrayList<String> = arrayListOf()
    private var partList: ArrayList<ApplicantData> = arrayListOf()
    private var id: Int = -1
    private var type: String = "" //초기는 TEMP
    private var limit: Int = -1
    private var peopleNum: Int = -1
    private var part: String = ""
    private var mainImg = ""
    private var title = ""

    private var partLimitLoadStatus = false
    private var backEnd = 0
    private var frontEnd = 0
    private var plan = 0
    private var design = 0
    private var etc = 0


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

        mainImg = intent.getStringExtra("mainImg").toString()
        title = intent.getStringExtra("title").toString()
        id = intent.getIntExtra("id",-1) //co_projectId로 전달해야함
        type = intent.getStringExtra("type").toString() //PROJECT or STUDY
        limit = intent.getIntExtra("limit",-1)


        loadData(this, type , id, "TEMP")

        //선택하기 버튼
        viewBinding.btnEdit.setOnClickListener {
            if (viewBinding.btnEdit.isChecked){
                viewBinding.btnEdit.text = "취소하기"
            }else{
                viewBinding.btnEdit.text = "선택하기"
            }
            Log.d("test",part)
            if (part == "TEMP"){
                viewBinding.bottomBtn.isGone1 = viewBinding.btnEdit.isChecked
                viewBinding.btnSelect1.isGone1 = !viewBinding.btnEdit.isChecked
            }else{
                viewBinding.btnSelect2.isGone1 = !viewBinding.btnEdit.isChecked
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
            viewBinding.btnSelect1.isGone1 = !viewBinding.btnEdit.isChecked
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
                Log.d("btnSelect2: 지원자 선택 목록",adapter2.getSelectList()[i].toString())
                //editApplicant(this,editList: List<String>)
            }
            Log.d("applicantList: 선택된 지원자 email 목록", applicantList.toString())

            editApplicant(this, type, id, adapter2.getSelectList())
            loadData(this, type, id, part)

            viewBinding.btnEdit.isChecked = !viewBinding.btnEdit.isChecked
            viewBinding.btnSelect2.isGone1 = !viewBinding.btnEdit.isChecked
            viewBinding.btnEdit.text = "선택하기"
            adapter2.setEdit(viewBinding.btnEdit.isChecked)
            adapter2.setCount(0)
            adapter2.resetSelectList()
            adapter2.resetIsChecked()
            adapter2.notifyDataSetChanged()
            enableDelete(false)
            enableRegist(false)


        }

        //모집완료 버튼
        viewBinding.btnDoneRecruit.setOnClickListener {
            val roomType = "OTM"
            val roomId = "${roomType}_${type}_${id}"
            val inviteList = arrayListOf<String>()
            val selectList = adapter2.getListData()
            for (i in selectList){
                inviteList.add(i.co_email)
                when(i.co_part){
                    "기획" -> plan--
                    "디자인" -> design--
                    "프론트엔드" -> frontEnd--
                    "백엔드" -> backEnd--
                    "기타" -> etc--
                }
            }

            if(plan>=0 && design>=0 && frontEnd>=0 && backEnd>=0 && etc>=0){
                Log.d("test",roomId)
                doneRecruit(this, type, id, applicantList)
                ChatClient.createChatRoom(this, title, mainImg, inviteList, roomType, type, id, optionMove = false)
                val intent = Intent(this, RecruitDoneActivity::class.java)
                intent.putExtra("roomId", roomId)
                intent.putExtra("selectList", selectList)
                startActivity(intent)
            }else{
                Toast.makeText(this, "파트별 제한인원을 다시 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        //초기화 버튼
        viewBinding.btnReset.setOnClickListener {
            //선택완료된 지원자 전체 리스트
            //Log.d("선택된 지원자 all: ", applicantList.toString())
            editApplicant(this, type, id, applicantList)
            loadData(this, type, id, part)

            //selectList.clear()
            Log.d("초기화 버튼 눌림. ", " ")
            loadData(this,type,id,part)
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

    private fun enableReset(boolean: Boolean){ //true가 넘어왔다 일단
        if(boolean == viewBinding.bottomBtn.isEnabled){
            viewBinding.btnReset.isEnabled = boolean
            viewBinding.btnReset.isSelected = boolean
            viewBinding.bottomBtn.isEnabled = !boolean
            viewBinding.btnReset.setTextColor(ContextCompat.getColor(this!!,R.color.black_700))
        }
    }
    private fun enableDone(boolean: Boolean){ //true가 넘어왔다 일단
        if(boolean != viewBinding.bottomBtn.isEnabled){
            viewBinding.btnDoneRecruit.isEnabled = boolean
            viewBinding.btnDoneRecruit.isSelected = boolean
            viewBinding.bottomBtn.isEnabled = !boolean
            viewBinding.btnDoneRecruit.setTextColor(ContextCompat.getColor(this!!,R.color.white))
        }
    }

    /*private fun setAdapter1(dataList: ArrayList<ApplicantData>, context: Context, limit: Int){
        Log.d("test", "어댑터 1")
        adapter1 = AdapterRecruitApplicants1(context, dataList, limit){
            Log.d("test","리콜받음 $it")
            loadData(context, type, id, it)
            viewBinding.btnEdit.isChecked = false
            viewBinding.btnEdit.text = "선택하기"
        }
        val currentPosition = (viewBinding.part.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        viewBinding.part.adapter = adapter1
        viewBinding.part.scrollToPosition(currentPosition)
    }*/
    private lateinit var hbinding: RecycleRecruitApplyPartHeaderBinding
    private lateinit var ibinding: RecycleRecruitApplyPartItemBinding

    private fun setAdapter1(dataList: ArrayList<ApplicantData>, context: Context, limit: Int){
        //dataList: ArrayList<ApplicantData>였음
        Log.d("test", "어댑터 1")

        // Drawable 리소스 가져오기
        val drawable = resources.getDrawable(R.drawable.recruit_apply_allpartbox)
        // 텍스트와 Drawable 함께 설정
        viewBinding.btnSelected.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        viewBinding.btnSelected.setText(" | "+limit.toString()+"명")
        //viewBinding.btnSelected.text = R.drawable.recruit_apply_allpartbox+" | "+limit+"명"

        viewBinding.btnPlan.text = "   기획 | "+dataList[0].co_limit+"명"
        viewBinding.btnDesign.text = "   디자인 | "+dataList[1].co_limit+"명"
        viewBinding.btnFrontend.text = "  프론트엔드 | "+dataList[2].co_limit+"명"
        viewBinding.btnBackend.text = "   백엔드 | "+dataList[3].co_limit+"명"
        viewBinding.btnEtc.text = "   기타 | "+dataList[4].co_limit+"명"

        viewBinding.radioGroup.setOnCheckedChangeListener { radioGroup, checkID ->
            when(checkID) {
                R.id.btn_selected -> {
                    loadData(context,type,id,"TEMP")
                }
                R.id.btn_frontend -> {
                    loadData(context,type,id,"프론트엔드")
                }
                R.id.btn_backend -> {
                    loadData(context,type,id,"백엔드")
                }
                R.id.btn_design -> {
                    loadData(context,type,id,"디자인")
                }
                R.id.btn_plan -> {
                    loadData(context,type,id,"기획")
                }
                R.id.btn_etc -> {
                    loadData(context,type,id,"기타")
                }
            }
        }

        viewBinding.btnEdit.isChecked = false
        viewBinding.btnEdit.text = "선택하기"


    }

    private fun setAdapter2(dataList: ArrayList<ApplicantInfoData>, context: Context){
        Log.d("test", "어댑터 2")
        adapter2 = AdapterRecruitApplicants2(context, dataList, id, type){
            Log.d("test","리콜받음")
            Log.d("setAdapter2 : returnCount :",it.toString())
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
            RetrofitClient.service.getApplyerProjectList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id, coPart).enqueue(object : Callback<ResApplyerList> {
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
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.result.message.co_applicantsInfo}")

                                    setAdapter1(it.result.message.co_applicantsCount, context, limit)
                                    setAdapter2(it.result.message.co_applicantsInfo, context)

                                    applicantList= ArrayList()
                                    for (i in it.result.message.co_applicantsInfo){
                                        applicantList.add(i.co_email)
                                    }

                                    if (!partLimitLoadStatus){
                                        partLimitLoadStatus = true
                                        for(i in  it.result.message.co_applicantsCount){
                                            when (i.co_part){
                                                "기획" ->{
                                                    plan = i.co_limit
                                                }
                                                "디자인" ->{
                                                    design = i.co_limit
                                                }
                                                "프론트엔드" -> {
                                                    frontEnd = i.co_limit
                                                }
                                                "백엔드" -> {
                                                    backEnd = i.co_limit
                                                }
                                                "기타" ->{
                                                    etc = i.co_limit
                                                }
                                            }
                                        }
                                    }

                                    Log.d("applicantList", applicantList.toString())

                                    part = it.result.message.co_part
                                    if (part == "TEMP"){
                                        viewBinding.bottomBtn.isGone1 = false
                                        viewBinding.btnSelect1.isGone1 = true
                                        viewBinding.btnSelect2.isGone1 = true
                                        peopleNum = it.result.message.co_tempSavedApplicantsCount
                                        viewBinding.applicantNum.text = "현재 선택한 지원자 $peopleNum"

                                        //viewBinding.btnSelected.text = "그림 | $peopleNum"+"명"


                                        if(peopleNum > 0){ //초기화, 모집완료 활성화 (담은 인원이 1명 이상)

                                            enableReset(true)
                                            enableDone(true)
                                        }else{
                                            enableReset(false)
                                            viewBinding.btnReset.setTextColor(ContextCompat.getColor(context!!,R.color.black_300))
                                            enableDone(false)
                                            viewBinding.btnDoneRecruit.setTextColor(ContextCompat.getColor(context!!,R.color.black_500))
                                        }


                                    }
                                    else{
                                        viewBinding.bottomBtn.isGone1 = true
                                        viewBinding.btnSelect1.isGone1 = true
                                        viewBinding.btnSelect2.isGone1 = true
                                        peopleNum = it.result.message.co_applicantsInfo.size
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
        else if(type == "STUDY") {
            RetrofitClient.service.getApplyerStudyList(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id, coPart).enqueue(object : Callback<ResApplyerList> {
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
                                    Log.d("test: 지원자 리스트 불러오기 성공", "\n${it.result.message.co_applicantsInfo}")
                                    if(!loadStatus){
                                        setAdapter1(it.result.message.co_applicantsCount, context, limit)
                                        loadStatus = true
                                    }

                                    var temp = ArrayList<ApplicantInfoData>()
                                    if(it.result.message.co_applicantsInfo != null){
                                        temp = it.result.message.co_applicantsInfo
                                    }
                                    setAdapter2(temp, context)

                                    applicantList = ArrayList()
                                    for (i in temp){
                                        applicantList.add(i.co_email)
                                    }

                                    if (!partLimitLoadStatus){
                                        partLimitLoadStatus = true
                                        for (i in it.result.message.co_applicantsCount){
                                            when (i.co_part){
                                                "기획" ->{
                                                    plan = i.co_limit
                                                }
                                                "디자인" ->{
                                                    design = i.co_limit
                                                }
                                                "프론트엔드" -> {
                                                    frontEnd = i.co_limit
                                                }
                                                "백엔드" -> {
                                                    backEnd = i.co_limit
                                                }
                                                "기타" ->{
                                                    etc = i.co_limit
                                                }
                                            }
                                        }
                                    }

                                    Log.d("applicantList", applicantList.toString())

                                    part = it.result.message.co_part
                                    if (part == "TEMP"){
                                        viewBinding.bottomBtn.isGone1 = false
                                        viewBinding.btnSelect1.isGone1 = true
                                        viewBinding.btnSelect2.isGone1 = true
                                        peopleNum = it.result.message.co_tempSavedApplicantsCount
                                        viewBinding.applicantNum.text = "현재 선택한 지원자 $peopleNum"


                                        if(peopleNum > 0){ //초기화, 모집완료 활성화 (담은 인원이 1명 이상)
                                            enableReset(true)
                                            enableDone(true)
                                        }else{
                                            enableReset(false)
                                            viewBinding.btnReset.setTextColor(ContextCompat.getColor(context!!,R.color.black_300))
                                            enableDone(false)
                                            viewBinding.btnDoneRecruit.setTextColor(ContextCompat.getColor(context!!,R.color.black_500))
                                        }


                                    }
                                    else{
                                        viewBinding.bottomBtn.isGone1 = true
                                        viewBinding.btnSelect1.isGone1 = true
                                        viewBinding.btnSelect2.isGone1 = true
                                        peopleNum = temp.size
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

    //모집완료 function
    private fun doneRecruit(context: Context, type: String, id: Int, recruitedList: ArrayList<String>){
        if (type == "PROJECT"){
            RetrofitClient.service.doneRecruitProject(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id, ReqRecruitedApplicantList(recruitedList)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 모집완료 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 모집완료 성공", "\n${it.toString()}")

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
            RetrofitClient.service.doneRecruitStudy(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id, ReqRecruitedApplicantList(recruitedList)).enqueue(object: Callback<JsonObject>{
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


    //지원자 EDIT
    private fun editApplicant(context: Context, type: String, id: Int, editList: List<String>){
        if (type == "PROJECT"){
            RetrofitClient.service.requestProjectApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id, ReqUpdateApplicant(editList)).enqueue(object: Callback<JsonObject>{
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
            RetrofitClient.service.requestStudyApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id, ReqUpdateApplicant(editList)).enqueue(object: Callback<JsonObject>{
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
