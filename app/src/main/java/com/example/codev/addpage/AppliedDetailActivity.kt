package com.example.codev.addpage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.codev.*
import com.example.codev.databinding.ActivityAppliedDetailBinding
import com.example.codev.databinding.InputLayoutBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppliedDetailActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAppliedDetailBinding
    private var isLoaded = false
    private var partName = ""
    private var isSelected = false
    private var nowPageType = "PROJECT"
    private var nowId = -1
    private var name = ""
    private var receiver_email = ""

    override fun onResume() {
        super.onResume()
        nowPageType = intent.getStringExtra("type")!!
        nowId = intent.getIntExtra("id", -1) // intent.putExtra("coProjectId", 값)
        val coPortfolioId = intent.getIntExtra("coPortfolioId", -1) // intent.putExtra("coPortfolioId", 값)
        partName = intent.getStringExtra("coPart").toString() // intent.putExtra("coPart", 값)
        isSelected = intent.getBooleanExtra("coTemporaryStorage", false) // intent.putExtra("coTemporaryStorage", 값)
        name = intent.getStringExtra("name").toString()
        receiver_email = intent.getStringExtra("receiver_email").toString()

        if(nowId < 0 || coPortfolioId < 0 || partName == ""){
            Toast.makeText(this, "조회 실패: 다시 시도해주세요(초기값 오류)", Toast.LENGTH_SHORT).show()
        }else{
            loadData(nowPageType, nowId, coPortfolioId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAppliedDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = "지원상세 보기"
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.addLinkSection.removeAllViews()
        viewBinding.stackChipGroup.removeAllViews()

        //테스트 코드
//        val intent = Intent(this@MainAppActivity, AppliedDetailActivity::class.java)
//        intent.putExtra("coProjectId", 65)
//        intent.putExtra("coPortfolioId", 68)
//        intent.putExtra("coPart", "abc")
//        intent.putExtra("coTemporaryStorage", false)
//        startActivity(intent)
        //테스트 코드


        viewBinding.btnLeft.setOnClickListener {
            if(isLoaded){
                Toast.makeText(this, "문의하기", Toast.LENGTH_SHORT).show()
                val roomType = "UTU"
                val roomId = "${roomType}_${UserSharedPreferences.getKey(this)}_${receiver_email}"
                val inviteList = arrayListOf<String>(receiver_email)
                Log.d("test",roomId)
                conFirmChatRoom(this, roomId, roomType, inviteList)
            }else{
                Toast.makeText(this, "로딩중입니다. 잠시만 기다려 주세요", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.btnRight.setOnClickListener {
            if(isLoaded){
                if(isSelected){
                    changePickStatus(this)
                    Toast.makeText(this, "선택 취소하기", Toast.LENGTH_SHORT).show()
                }else{
                    changePickStatus(this)
                    Toast.makeText(this, "선택하기", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "로딩중입니다. 잠시만 기다려 주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun conFirmChatRoom(context: Context, roomId: String, roomType: String, inviteList: ArrayList<String>){
        RetrofitClient.service.confirmChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),roomId).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방생성 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("stomp: 채팅방 새로 개설", "새로 생성")
                                createChat(context, roomId, roomType, inviteList)
                            }
                        }
                        401 ->{
                            Log.d("stomp: 채팅방 이미 존재", "이미 생성")
                            ChatClient.join(context, roomId)
                            val intent = Intent(context, ChatRoomActivity::class.java)
                            intent.putExtra("title", name)
                            intent.putExtra("roomId", roomId)
                            intent.putExtra("people", 1)
                            intent.putExtra("isRead", 0)
                            startActivity(intent)
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

    private fun createChat(context: Context, roomId: String, roomType: String, inviteList: ArrayList<String>){
        RetrofitClient.service.createChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), ReqCreateChatRoom(roomId, roomType, receiver_email, null)).enqueue(object: Callback<JsonObject> {
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
                                intent.putExtra("title", name)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun loadData(nowType: String, id: Int, portfolioId: Int){
        if(nowType == "PROJECT"){
            RetrofitClient.service.getProjectAppliedDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this)), id, portfolioId).enqueue(object:
                Callback<ResAppliedUserDetail> {
                override fun onResponse(
                    call: Call<ResAppliedUserDetail>,
                    response: Response<ResAppliedUserDetail>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d("test: 포트폴리오 불러오기 실패", response.toString())
                        Toast.makeText(
                            this@AppliedDetailActivity,
                            "서버와 연결을 시도했으나 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 포트폴리오 불러오기 성공", response.toString())
                                nowPageType = "PROJECT"
                                setDataOnPage(this@AppliedDetailActivity, it.result.message)
                                isLoaded = true
                            }
                        }
                        400 -> {
                            Toast.makeText(this@AppliedDetailActivity, "지원자가 해당 포트폴리오를 삭제했습니다.", Toast.LENGTH_SHORT).show()
                            setEmptyPage(this@AppliedDetailActivity)
                            //isLoaded 처리 필요
                        }

                    }

                }

                override fun onFailure(call: Call<ResAppliedUserDetail>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }else if(nowType == "STUDY"){
            RetrofitClient.service.getStudyAppliedDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(this)), id, portfolioId).enqueue(object:
                Callback<ResAppliedUserDetail> {
                override fun onResponse(
                    call: Call<ResAppliedUserDetail>,
                    response: Response<ResAppliedUserDetail>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d("test: 포트폴리오 불러오기 실패", response.toString())
                        Toast.makeText(
                            this@AppliedDetailActivity,
                            "서버와 연결을 시도했으나 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 포트폴리오 불러오기 성공", response.toString())
                                nowPageType = "STUDY"
                                setDataOnPage(this@AppliedDetailActivity, it.result.message)
                                isLoaded = true
                            }
                        }
                        400 -> {
                            Toast.makeText(this@AppliedDetailActivity, "지원자가 해당 포트폴리오를 삭제했습니다.", Toast.LENGTH_SHORT).show()
                            setEmptyPage(this@AppliedDetailActivity)
                            //isLoaded 처리 필요

                        }

                    }

                }

                override fun onFailure(call: Call<ResAppliedUserDetail>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun setDataOnPage(context: Context, pfData: AllAppliedUserDetailMessage){
        //setImage
        Glide.with(context).load(pfData.profileImg).error(R.drawable.my_profile).circleCrop().into(viewBinding.userImage)

        viewBinding.userName.setText(pfData.co_name) //name

        //gender
        var genderText = ""
        genderText = if (pfData.co_gender == "MALE") "남성"
        else "여성"
        viewBinding.userGender.text = genderText
        viewBinding.userBirth.text = pfData.co_birth.replace("-", "/")
        viewBinding.applyPartText.text = partName
        viewBinding.editApplyContent.text = pfData.co_motivation

        viewBinding.pfTitle.text = pfData.co_title
        viewBinding.editPfLevel.text = pfData.co_rank
        if(!pfData.co_languages.isNullOrBlank()){
            val getStackNameList = pfData.co_languages.split(",")
            for (i in getStackNameList) viewBinding.stackChipGroup.addView(
                AddPageFunction().returnStackChipWithPF(
                    context,
                    i
                )
            )
        }

        viewBinding.editPfIntro.text = pfData.co_headLine
        viewBinding.editPfContent.text = pfData.co_introduction
        viewBinding.textCounter.text = pfData.co_introduction.length.toString()

        //setLink
        if(!pfData.co_links.isNullOrBlank()){
            val linkList = pfData.co_links.split(",")
            Log.d("linktest", "setData: $linkList")
            for (i in linkList) {
                val linkView = InputLayoutBinding.inflate(layoutInflater)
                linkView.inputOfTitle.setText(i)
                linkView.inputOfTitle.isFocusable = false
                linkView.inputOfTitle.isClickable = false
                viewBinding.addLinkSection.addView(linkView.root)
                Log.d("nowLink", "setData: $i")
            }
        }

        if(isSelected){
            viewBinding.btnRight.text = "선택 취소하기"
            viewBinding.btnRight.background = getDrawable(R.drawable.recruit_detail_btn2_selected)
        }
    }

    private fun setEmptyPage(context: Context){
        //setImage
        Glide.with(context).load(R.drawable.my_profile).circleCrop().into(viewBinding.userImage)

        viewBinding.userName.setText("삭제된 포토폴리오입니다.") //name

        //gender
        var genderText = "삭제된 포토폴리오입니다."
//        genderText = if (pfData.co_gender == "MALE") "남성"
//        else "여성"
        viewBinding.userGender.text = genderText
        viewBinding.userBirth.text = "없음"
        viewBinding.applyPartText.text = "삭제된 포토폴리오입니다."
        viewBinding.editApplyContent.text = "삭제된 포토폴리오입니다."

        viewBinding.pfTitle.text = "삭제된 포토폴리오입니다."
        viewBinding.editPfLevel.text = "삭제된 포토폴리오입니다."

        viewBinding.editPfIntro.text = "삭제된 포토폴리오입니다."
        viewBinding.editPfContent.text = "삭제된 포토폴리오입니다."
        viewBinding.textCounter.text = "0"

        //불러오지 못하면 선택하기 및 문의하기를 처리하는 방법 필요

//        if(isSelected){
//            viewBinding.btnRight.text = "선택 취소하기"
//            viewBinding.btnRight.background = getDrawable(R.drawable.recruit_detail_btn2_selected)
//        }
    }

    private fun changePickStatus(context: Context){
        viewBinding.btnRight.isSelected = false
        viewBinding.btnRight.isEnabled = false
        if(nowPageType == "PROJECT"){
            RetrofitClient.service.requestProjectApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), nowId, ReqUpdateApplicant(listOf(receiver_email))).enqueue(
                object : Callback<JsonObject>{
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if(response.isSuccessful.not()){
                            Log.d("test: 지원자 편집 실패",response.toString())
                            Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                            viewBinding.btnRight.isSelected = true
                            viewBinding.btnRight.isEnabled = true
                        }
                        when(response.code()){
                            200 -> {
                                response.body()?.let {
                                    Log.d("test: 지원자 편집 성공", "\n${it.toString()}")
                                    finish()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("pickFail", t.toString())
                        viewBinding.btnRight.isSelected = true
                        viewBinding.btnRight.isEnabled = true
                    }
                })
        }else if(nowPageType == "STUDY"){
            RetrofitClient.service.requestStudyApplicant(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), nowId, ReqUpdateApplicant(listOf(receiver_email))).enqueue(
                object : Callback<JsonObject>{
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if(response.isSuccessful.not()){
                            Log.d("test: 지원자 편집 실패",response.toString())
                            Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                            viewBinding.btnRight.isSelected = true
                            viewBinding.btnRight.isEnabled = true
                        }
                        when(response.code()){
                            200 -> {
                                response.body()?.let {
                                    Log.d("test: 지원자 편집 성공", "\n${it.toString()}")
                                    finish()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("pickFail", t.toString())
                        viewBinding.btnRight.isSelected = true
                        viewBinding.btnRight.isEnabled = true
                    }
                })
        }
    }
}