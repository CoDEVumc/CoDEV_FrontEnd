package com.example.codev

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.codev.addpage.*
import com.example.codev.databinding.ActivityRecruitDetailBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat


class RecruitDetailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitDetailBinding
    private var id: Int = -1
    private var type: String = ""
    private var dday: String = ""
    private var limit: Int = -1
    private lateinit var studyData: EditStudy
    private lateinit var projectData: EditProject
    private var partList: ArrayList<RecruitPartLimit> = arrayListOf()
    private var recruitStatus: Boolean = false
    private var writer: String = ""
    private var process: String = ""

    override fun onResume() {
        super.onResume()
        viewBinding.toolbarRecruit.toolbar3.menu.clear()
        id = intent.getIntExtra("id",-1)
        type = intent.getStringExtra("type").toString()
        dday = intent.getStringExtra("dday").toString()
        Log.d("test: 상세페이지 넘어온 type, id", "$type : $id")
        if (id != -1) {
            loadRecruitDetail(this, type, id, dday)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRecruit.toolbar3.title = ""
        setSupportActionBar(viewBinding.toolbarRecruit.toolbar3)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.heart.setOnClickListener {
            request(this, type, id)
            if (viewBinding.heart.isChecked){
                viewBinding.heartCount.text = (viewBinding.heartCount.text.toString().toInt() + 1).toString()
            }else{
                viewBinding.heartCount.text = (viewBinding.heartCount.text.toString().toInt() - 1).toString()
            }
        }

        //스크롤 위치가 이미지 indicator 정도 오면 툴바 배경 흰색으로 변경
        val toolbarHeight = dpToPx(this@RecruitDetailActivity,56.0f)
        var toolbarColor: String = ""
        viewBinding.scrollView.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                Log.d("test", "indicator의 top Y좌표 : " +  viewBinding.indicator.top + ", 현재 스크롤 Y좌표 + 툴바 높이 : " + (scrollY + toolbarHeight))
                if (scrollY + toolbarHeight >= viewBinding.indicator.top){
                    if (toolbarColor != "WHITE"){
                        viewBinding.toolbarRecruit.toolbar3.setBackgroundColor(getColor(R.color.white))
                        toolbarColor = "WHITE"
                    }
                }else{
                    if (toolbarColor != "TRANSPARENT"){
                        viewBinding.toolbarRecruit.toolbar3.setBackgroundColor(getColor(R.color.transparent))
                        toolbarColor = "TRANSPARENT"
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.menu_modify -> {
                if (type == "PROJECT"){
                    val intent = Intent(this,AddNewProjectActivity::class.java)
                    intent.putExtra("project",projectData)
                    startActivity(intent)
                }else if(type == "STUDY"){
                    val intent = Intent(this,AddNewStudyActivity::class.java)
                    intent.putExtra("study",studyData)
                    startActivity(intent)
                }
            }
            R.id.menu_delete -> {
                confirmDelete(this, id) { finish() }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //글 작성자 설정, 연장히기와 지원현황 기능 필요
    private fun setWriterMode(context: Context, type: String){
        menuInflater.inflate(R.menu.menu_toolbar_detail, viewBinding.toolbarRecruit.toolbar3.menu)
        viewBinding.btn1.text = "연장하기"
        viewBinding.btn2.text = "지원현황"
        viewBinding.btn1.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val dateShowString = "${year}/${month+1}/${dayOfMonth}"
                val dateJsonString = String.format("%d-%02d-%d", year, month + 1, dayOfMonth)
                Log.d("test",dateJsonString)
                extend(context, type, id, dateJsonString)
            }
            val dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(
                Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.datePicker.maxDate = (System.currentTimeMillis() + 3.156e+10).toLong()
            dpd.show()
        }
        //지현현황 리스트
        viewBinding.btn2.setOnClickListener {
            val intent = Intent(context, RecruitApplyListActivity::class.java)
            intent.putExtra("limit", limit)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            startActivity(intent)
        }
    }

    //지원했으며 모집중인 상태와 심사중과 모집완료 상태 설정, 지원취소 기능필요
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setViewerMode(context: Context, type: String, status: Boolean, process: String){
        if (status && process == "ING"){
            viewBinding.btn2.isSelected = true
            viewBinding.btn2.text = "지원취소"
            viewBinding.btn2.setOnClickListener {
                cancel(context, type, id)
            }
        } else if(process!="ING"){
            viewBinding.btn2.text = "지원마감"
            viewBinding.btn2.setTextColor(getColor(R.color.black_500))
            viewBinding.btn2.background = getDrawable(R.drawable.recruit_detail_btn2_disabled)
            viewBinding.btn2.isEnabled = false
        }else{
            //기본은 지원하기
            viewBinding.btn2.setOnClickListener {
                val intent = Intent(context, RecruitApplyActivity::class.java)
                intent.putExtra("recruitId", id)
                intent.putExtra("type", type)
                intent.putExtra("writer", writer)
                intent.putExtra("process", process)
                intent.putExtra("recruitStatus", recruitStatus)
                intent.putExtra("partList", partList)
                startActivity(intent)
            }
        }
        //기본은 문의하기
        viewBinding.btn1.setOnClickListener {

            val roomType = "OTO"
            val roomId = "${roomType}_${type}_${id}_${UserSharedPreferences.getKey(this)}"
            val inviteList = arrayListOf<String>(writer)
            Log.d("test",roomId)
            ChatClient.join(this, roomId)
//            createChat(this, roomId, roomType, inviteList)
        }
    }

    private fun createChat(context: Context, roomId: String, roomType: String, inviteList: ArrayList<String>){
        RetrofitClient.service.createChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), ReqCreateChatRoom(roomId, roomType, viewBinding.title.text.toString(), null)).enqueue(object: Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방생성 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 채팅방생성 성공! ", "\n${it.toString()}")
                                inviteChat(context, roomId, inviteList)
                            }
                        }
                        401 ->{
                            Log.d("test: 401", "이미 생성")
                            Toast.makeText(context, "이미 문의 채팅이 생성되어있습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: 채팅방생성 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun inviteChat(context: Context, roomId: String, inviteList: ArrayList<String>){
        RetrofitClient.service.inviteChat(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), ReqInviteChat(roomId, inviteList)).enqueue(object: Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방초대 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 채팅방초대 성공! ", "\n${it.toString()}")
                                ChatClient.join(context, roomId)
                                val intent = Intent(context, ChatRoomActivity::class.java)
                                intent.putExtra("title", viewBinding.name.text.toString())
                                intent.putExtra("roomId", roomId)
                                intent.putExtra("people", 1)
                                intent.putExtra("isRead", 0)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: 채팅방초대 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //모집글 삭제
    private fun confirmDelete(context: Context, id: Int, finishPage: () -> Unit){
        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
        val builder = AlertDialog.Builder(context)
        builder.setTitle("모집글 삭제")
            .setMessage("해당 글을 정말로 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, _ ->
                    if (type == "PROJECT"){
                        RetrofitClient.service.deleteProject(id,AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object: Callback<JsonObject>{
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                if(response.isSuccessful.not()){
                                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                when(response.code()){
                                    200 -> {
                                        response.body()?.let {
                                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                                            finishPage()
                                        }
                                    }
                                }
                            }
                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                Log.d("test", "[Fail]${t.toString()}")
                            }
                        })
                    }else if(type == "STUDY"){
                        RetrofitClient.service.deleteStudy(id,AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object: Callback<JsonObject>{
                            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                if(response.isSuccessful.not()){
                                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                when(response.code()){
                                    200 -> {
                                        response.body()?.let {
                                            Log.d("test: 포트폴리오 삭제 성공", "\n${it}")
                                            finishPage()
                                        }
                                    }
                                }
                            }
                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                Log.d("test", "[Fail]${t.toString()}")
                            }
                        })
                    }
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, _ ->
                    Toast.makeText(context, "취소함", Toast.LENGTH_SHORT).show()
                })
        // 다이얼로그를 띄워주기
        builder.show()
    }

    private fun loadRecruitDetail(context:Context, type:String, id: Int, dday: String){
        if (type == "PROJECT"){
            RetrofitClient.service.getProjectDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<ResGetRecruitDetail> {
                @SuppressLint("UseCompatLoadingForDrawables")
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
                                    partList = it.result.Complete.co_partList
                                    setPartAdapter(context,partList)
                                    setImageAdapter(context,it.result.Complete.co_photos)

                                    viewBinding.type.text = "프로젝트"
                                    viewBinding.name.text = it.result.Complete.co_nickname
                                    viewBinding.title.text = it.result.Complete.co_title
                                    viewBinding.location.text = it.result.Complete.co_location
                                    viewBinding.day.text = dday
                                    viewBinding.text.text = it.result.Complete.co_content
                                    viewBinding.date.text = convertTimestampToDate(it.result.Complete.updatedAt)
                                    viewBinding.deadline.text = it.result.Complete.co_deadLine.split(" ")[0].replace("-",".")
                                    limit = it.result.Complete.co_total
                                    viewBinding.total.text = it.result.Complete.co_total.toString() + "명 모집중"
                                    viewBinding.heartCount.text = it.result.Complete.co_heartCount.toString()
                                    viewBinding.heart.isChecked = it.result.Complete.co_heart

                                    process = it.result.Complete.co_process
                                    recruitStatus = it.result.Complete.co_recruitStatus
                                    writer = it.result.Complete.co_email

                                    //글 작성자, 글 관찰자 설정
                                    if (writer == it.result.Complete.co_viewer){
                                        setWriterMode(context, type)
                                    }else{
                                        setViewerMode(context, type, recruitStatus, process)
                                    }

                                    val stackList = LinkedHashMap<Int, String>()
                                    for (i in 0 until it.result.Complete.co_languageList.size){
                                        stackList[it.result.Complete.co_languageList[i].co_languageId] = it.result.Complete.co_languageList[i].co_language
                                    }

                                    projectData = EditProject(it.result.Complete.co_projectId.toString(),it.result.Complete.co_title,it.result.Complete.co_content,it.result.Complete.co_photos,it.result.Complete.co_partList,stackList,it.result.Complete.co_location,it.result.Complete.co_deadLine.split(" ")[0])
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
            RetrofitClient.service.getStudyDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<ResGetRecruitDetail> {
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
                                    partList = arrayListOf(RecruitPartLimit(it.result.Complete.co_part,it.result.Complete.co_total))
                                    setPartAdapter(context, partList)
                                    setImageAdapter(context,it.result.Complete.co_photos)
                                    viewBinding.type.text = "스터디"
                                    viewBinding.name.text = it.result.Complete.co_nickname
                                    viewBinding.title.text = it.result.Complete.co_title
                                    viewBinding.location.text = it.result.Complete.co_location
                                    viewBinding.day.text = dday
                                    viewBinding.text.text = it.result.Complete.co_content
                                    viewBinding.date.text = convertTimestampToDate(it.result.Complete.updatedAt)
                                    viewBinding.deadline.text = it.result.Complete.co_deadLine.split(" ")[0].replace("-",".")
                                    limit = it.result.Complete.co_total
                                    viewBinding.total.text = it.result.Complete.co_total.toString() + "명 모집중"
                                    viewBinding.heartCount.text = it.result.Complete.co_heartCount.toString()
                                    viewBinding.heart.isChecked = it.result.Complete.co_heart

                                    process = it.result.Complete.co_process
                                    recruitStatus = it.result.Complete.co_recruitStatus
                                    writer = it.result.Complete.co_email

                                    //글 작성자, 글 관찰자 설정
                                    if (writer == it.result.Complete.co_viewer){
                                        setWriterMode(context, type)
                                    }else{
                                        setViewerMode(context, type, recruitStatus, process)
                                    }

                                    val stackList = LinkedHashMap<Int, String>()
                                    for (i in 0 until it.result.Complete.co_languageList.size){
                                        stackList[it.result.Complete.co_languageList[i].co_languageId] = it.result.Complete.co_languageList[i].co_language
                                    }

                                    studyData = EditStudy(it.result.Complete.co_projectId.toString(),it.result.Complete.co_title,it.result.Complete.co_content,it.result.Complete.co_photos,it.result.Complete.co_part, it.result.Complete.co_total,stackList,it.result.Complete.co_location,it.result.Complete.co_deadLine.split(" ")[0])
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

    private fun request(context: Context, type: String, id: Int){
        if(type == "PROJECT"){
            RetrofitClient.service.requestProjectBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 북마크 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: 북마크 성공! ", "\n${it.toString()}")

                                }

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test: 북마크 실패 : ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            })
        }else if(type == "STUDY"){
            RetrofitClient.service.requestStudyBookMark(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id).enqueue(object: Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 북마크 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: 북마크 성공! ", "\n${it.toString()}")

                                }

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test: 북마크 실패 : ", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun cancel(context: Context, type: String, id: Int){
        Log.d("test",process)
        if (type == "PROJECT"){
            RetrofitClient.service.cancelProject(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqCancelRecruit(recruitStatus, writer, process)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 취소 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 취소 성공", "\n${it.toString()}")
                                val intent = intent
                                finish()
                                startActivity(intent)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }else if(type == "STUDY"){
            RetrofitClient.service.cancelStudy(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqCancelRecruit(recruitStatus, writer, process)).enqueue(object: Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 취소 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 취소 성공", "\n${it.toString()}")
                                val intent = intent
                                finish()
                                startActivity(intent)
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

    private fun extend(context: Context, type: String, id: Int, deadLine: String){
        if (type == "PROJECT"){
            RetrofitClient.service.extendProject(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqExtendProject(deadLine)).enqueue(object: Callback<ResExtendRecruit>{
                override fun onResponse(call: Call<ResExtendRecruit>, response: Response<ResExtendRecruit>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 연장 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {

                                when (it.code){
                                    200 ->{
                                        Log.d("test: 연장 성공", "\n${it.toString()}")
                                        val intent = intent
                                        finish()
                                        startActivity(intent)
                                    }
                                    446 ->{
                                        Toast.makeText(context, "이미 모집 마감된 스터디입니다", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResExtendRecruit>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }else if(type == "STUDY"){
            RetrofitClient.service.extendStudy(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), id, ReqExtendStudy(deadLine)).enqueue(object: Callback<ResExtendRecruit>{
                override fun onResponse(call: Call<ResExtendRecruit>, response: Response<ResExtendRecruit>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 연장 실패",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    when(response.code()){
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 연장 성공", "\n${it.toString()}")
                                val intent = intent
                                finish()
                                startActivity(intent)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResExtendRecruit>, t: Throwable) {
                    Log.d("test", "[Fail]${t.toString()}")
                }
            })
        }
    }

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertTimestampToDate(timestamp: Timestamp):String {
        val sdf = SimpleDateFormat("yyyy.MM.dd")
        val date = sdf.format(timestamp)
        Log.d("TTT UNix Date -> ", sdf.format((System.currentTimeMillis())).toString())
        Log.d("TTTT date -> ", date.toString())
        return date.toString()
    }
}