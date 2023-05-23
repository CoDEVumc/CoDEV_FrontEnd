package com.example.codev.userinfo

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.isGone
import com.example.codev.*
import com.example.codev.databinding.ActivityPasswordChangeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordChangeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityPasswordChangeBinding
    private var oldPasswordLength = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //가운데 정렬 글 작성 예시
        viewBinding.toolbarTitle.toolbarAddPageToolbar.title = ""
        viewBinding.toolbarTitle.toolbarText.text = "비밀번호 변경"
        viewBinding.toolbarTitle.toolbarText.typeface = Typeface.DEFAULT_BOLD //Text bold
        viewBinding.toolbarTitle.toolbarText.textSize = 16f //TextSixe = 16pt
        viewBinding.toolbarTitle.toolbarText.setTextColor(getColor(R.color.black))//TextColor = 900black

        setSupportActionBar(viewBinding.toolbarTitle.toolbarAddPageToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val regex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[~_!@#${'$'}%^&*()+|=])[A-Za-z\d~_!@#${'$'}%^&*()+|=]{8,}${'$'}""")
        viewBinding.warn1.isGone = true
        viewBinding.warn2.isGone = true
        viewBinding.warnOld.isGone = true

        viewBinding.etOldPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if(viewBinding.etOldPassword.text.matches(regex)){
//                    viewBinding.warnOld.isGone = true
//                    viewBinding.etOldPassword.setBackgroundResource(R.drawable.login_et)
//                }else{
//                    viewBinding.warnOld.isGone = false
//                    viewBinding.etOldPassword.setBackgroundResource(R.drawable.login_et_failed)
//                }
                oldPasswordLength = viewBinding.etOldPassword.text.length
                checkNextBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(viewBinding.etPassword.text.matches(regex)){
                    viewBinding.warn1.isGone = true
                    viewBinding.etPassword.setBackgroundResource(R.drawable.login_et)
                }else{
                    viewBinding.warn1.isGone = false
                    viewBinding.etPassword.setBackgroundResource(R.drawable.login_et_failed)
                }
                checkNextBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etPasswordChk.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!viewBinding.etPassword.text.isNullOrBlank() and (viewBinding.etPasswordChk.text.toString() == viewBinding.etPassword.text.toString())){
                    viewBinding.warn2.isGone = true
                    viewBinding.etPasswordChk.setBackgroundResource(R.drawable.login_et)
                    viewBinding.etPasswordChkImg.setImageResource(R.drawable.register_chk_checked)
                    checkNextBtn()
                }else{
                    viewBinding.warn2.isGone = false
                    viewBinding.etPasswordChk.setBackgroundResource(R.drawable.login_et_failed)
                    viewBinding.etPasswordChkImg.setImageResource(R.drawable.register_unchecked)
                    checkNextBtn()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.btnChangePassword.setOnClickListener {
            val oldPassword = viewBinding.etOldPassword.text.toString()
            val newPassword = viewBinding.etPassword.text.toString()
            nextBtnEnable(false) // 나중에 풀어줘야 해!
            //retrofit -> 원래 페이지로 돌아가기
            Log.d("TestPassword", "옜 비번: $oldPassword")
            Log.d("TestPassword", "새로운 비번: $newPassword")
            Log.d("testChangePasswordToken",
                AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken())
            )
            RetrofitClient.service.changePassword(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), ReqChangeUserPassword(oldPassword, newPassword)).enqueue(object:
                Callback<ResChangeUserPassword> {
                override fun onResponse(
                    call: Call<ResChangeUserPassword>,
                    response: Response<ResChangeUserPassword>
                ) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 비밀번호 변경 실패",response.toString())
                        Toast.makeText(this@PasswordChangeActivity, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                        nextBtnEnable(true)
                    }
                    when(response.code()){
                        200 -> {
                            Log.d("test: 비밀번호 변경 성공",response.toString())
                            Toast.makeText(this@PasswordChangeActivity, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        401 -> {
                            Log.d("test: 비밀번호 변경 실패",response.toString())
                            Toast.makeText(this@PasswordChangeActivity, "기존 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                            nextBtnEnable(true)
                        }
                    }
                }

                override fun onFailure(call: Call<ResChangeUserPassword>, t: Throwable) {
                    Log.d("testChangePassword", "onFailure: ${t.toString()}")
                    Toast.makeText(this@PasswordChangeActivity, "비밀번호 변경이 실패했습니다.", Toast.LENGTH_SHORT).show()
                    nextBtnEnable(true)
                }
            })
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

    private fun checkNextBtn() {
        if(viewBinding.warn1.isGone and viewBinding.warn2.isGone and (oldPasswordLength != 0) and (viewBinding.etPassword.text.length != 0) and (viewBinding.etPasswordChk.text.length != 0)){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
    }

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnChangePassword.isSelected != boolean){
            viewBinding.btnChangePassword.isSelected = boolean
            viewBinding.btnChangePassword.isEnabled = boolean
            if(boolean){
                viewBinding.btnChangePassword.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnChangePassword.setTextColor(getColor(R.color.black_500))
            }
        }
    }
}