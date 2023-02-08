package com.example.codev

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.example.codev.databinding.ActivityMyProfileBinding
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity:AppCompatActivity() {
    lateinit var viewBinding: ActivityMyProfileBinding
    private var isDefaultImg = false
    private val defaultImgUrl = "http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "내 프로필 관리"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val userinfo = intent.getSerializableExtra("userinfo") as Userinfo
        viewBinding.etName.setText(userinfo.co_name)
        viewBinding.etNickname.setText(userinfo.co_nickName)
        Glide.with(this)
            .load(userinfo.profileImg).circleCrop()
            .into(viewBinding.profileImg)
        if(userinfo.profileImg == defaultImgUrl) isDefaultImg = true
        viewBinding.countName.text = (5 - viewBinding.etName.text.length).toString()
        viewBinding.countNickname.text = (8 - viewBinding.etNickname.text.length).toString()

        //이름 검토
        viewBinding.warnName.isGone = true
        val nameRegex = Regex("""^[ㄱ-ㅎ|가-힣|a-z|A-Z|]+${'$'}""")
        viewBinding.etName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewBinding.countName.text = (5 - viewBinding.etName.text.length).toString()
                if(viewBinding.etName.text.matches(nameRegex)){
                    viewBinding.warnName.isGone = true
                    viewBinding.etName.setBackgroundResource(R.drawable.login_et)
                }else{
                    viewBinding.warnName.isGone = false
                    viewBinding.etName.setBackgroundResource(R.drawable.login_et_failed)
                }
                checkNextBtn()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        //닉네임 검토
        viewBinding.warnNickname.isGone = true
        val nickNameRegex = Regex("""^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+${'$'}""")
        viewBinding.etNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.countNickname.text = (8 - viewBinding.etNickname.text.length).toString()
                if(viewBinding.etNickname.text.matches(nickNameRegex)){
                    viewBinding.warnNickname.isGone = true
                    viewBinding.etNickname.setBackgroundResource(R.drawable.login_et)
                }else{
                    viewBinding.warnNickname.isGone = false
                    viewBinding.etNickname.setBackgroundResource(R.drawable.login_et_failed)
                }
                checkNextBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //프로필 변경 저장하기 기능 필요
        viewBinding.btnSave.setOnClickListener {
            uploadUserInfo(this)
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

    private fun uploadUserInfo(context: Context){
        //토큰 가져오기
        val userToken = AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))
        Log.d("postAuth", userToken)

        val finalName = viewBinding.etName.text.toString()
        val finalNickname = viewBinding.etNickname.text.toString()

        //텍스트 객체 만들고 인코딩하기
        val finalUserChangedClass = ReqUserInfoChanged(finalName, finalNickname)
        Log.d("finalUserChangedClass", "$finalUserChangedClass")
        val jsonObject = Gson().toJson(finalUserChangedClass)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject)

        //사진 다운로드 및 인코딩하기
        val emptyFileBody = RequestBody.create(MediaType.parse("application/octet-stream"), "")
        val emptyFilePart = MultipartBody.Part.createFormData("file", null, emptyFileBody)
        var finalFile = emptyFilePart
        if(!isDefaultImg){
            //선택된 이미지 업로드
        }


        //retrofit으로 올리고 사진 삭제하기
        RetrofitClient.service.changeUserInfo(userToken, requestBody, finalFile).enqueue(object :
            Callback<ResUserInfoChanged> {
            override fun onResponse(
                call: Call<ResUserInfoChanged>,
                response: Response<ResUserInfoChanged>
            ) {
                if(response.isSuccessful.not()){
                    Log.d("fail", "유저 변경 정보 업로드 실패: ${response.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body().let {
                            Log.d("success", "유저 변경 내용 업로드 성공: ${it.toString()}")
                            if(!isDefaultImg){
                                //업로드했던 이미지 삭제
                            }


                            finish()
                        }
                    }
                }

            }

            override fun onFailure(call: Call<ResUserInfoChanged>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun checkNextBtn() {
        if(viewBinding.warnName.isGone and viewBinding.warnNickname.isGone){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
    }

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnSave.isSelected != boolean){
            viewBinding.btnSave.isSelected = boolean
            viewBinding.btnSave.isEnabled = boolean
            if(boolean){
                viewBinding.btnSave.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnSave.setTextColor(getColor(R.color.black_500))
            }
        }
    }
}