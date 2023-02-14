package com.example.codev.userinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.codev.*
import com.example.codev.databinding.ActivityUserInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = "회원정보 수정"
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        loadData(this)
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

    private fun loadData(context: Context){
        RetrofitClient.service.getPortFolio(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context))).enqueue(object:
            Callback<ResPortFolioList> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResPortFolioList>, response: Response<ResPortFolioList>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 포트폴리오 불러오기 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                when(response.code()){
                    200 -> {
                        response.body()?.let {
                            Log.d("test: 사용자 정보 불러오기 성공", "\n${it.toString()}")
                            setLoginMethod(it.result.Complete.co_loginType)
                            setGenderText(it.result.Complete.co_gender)
                            viewBinding.userBirthText.text = it.result.Complete.co_birth.replace("-", "/")
                            viewBinding.userEmailText.text = it.result.Complete.co_email
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResPortFolioList>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }
        })
    }

    private fun setLoginMethod(loginType: String){
        when (loginType) {
            "CODEV" -> {
                viewBinding.textLoginMethod.text = "이메일"
                viewBinding.textPasswordNoti.text = "아래에서 비밀번호를 변경하실 수 있습니다."
                viewBinding.userPasswordSection.visibility = View.VISIBLE
                viewBinding.passwordChangeButtonSection.setOnClickListener {
                    val intent = Intent(this, PasswordChangeActivity::class.java)
                    startActivity(intent)
                }
            }
            "GOOGLE" -> {
                viewBinding.textLoginMethod.text = "Google"
                viewBinding.textPasswordNoti.text = "해당 SNS 계정에서 비밀번호를 변경하실 수 있습니다."
            }
            "GITHUB" -> {
                viewBinding.textLoginMethod.text = "Github"
                viewBinding.textPasswordNoti.text = "해당 SNS 계정에서 비밀번호를 변경하실 수 있습니다."
            }
        }
    }

    private fun setGenderText(genderText: String){
        if(genderText == "MALE"){
            viewBinding.userGenderText.text = "남성"
        }else{
            viewBinding.userGenderText.text = "여성"
        }
    }
}