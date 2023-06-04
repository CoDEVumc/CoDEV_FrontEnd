package com.codev.android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import com.codev.android.databinding.ActivityRegisterEmailBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterEmailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterEmailBinding
    private lateinit var reqSignUp: ReqSignUp
    private var isLoaded = true
    private var isLegal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.warn.isGone = true

        reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp

        val regex = Regex("""^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+${'$'}""")

        viewBinding.etEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isLegal = false
                nextBtnEnable(false)
                if (viewBinding.etEmail.text.matches(regex)){
                    viewBinding.warn.isGone = true
                    emailCheckBtnEnable(true)
                    viewBinding.etEmail.setBackgroundResource(R.drawable.login_et)
                } else{
                    viewBinding.warn.isGone = false
                    emailCheckBtnEnable(false)
                    viewBinding.etEmail.setBackgroundResource(R.drawable.login_et_failed)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.btnEmailCheck.setOnClickListener {
            checkEmail()
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            reqSignUp.co_email = viewBinding.etEmail.text.toString()
            val intent = Intent(this,RegisterPwdActivity::class.java)
            intent.putExtra("signUp",reqSignUp)
            startActivity(intent)

            //코드 인증 잠시 보류
            //sendEmailCode(this,viewBinding.etEmail.text.toString())
        }
    }

    private fun sendEmailCode(context: Context, email: String){
        RetrofitClient.service.getEmailCode(email).enqueue(object: Callback<ResGetEmailCode>{
            override fun onResponse(call: Call<ResGetEmailCode>, response: Response<ResGetEmailCode>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 이메일 코드보내기 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 이메일 코드보내기 성공", "\n${it.toString()}")
                                reqSignUp.co_email = email
                                val intent = Intent(context,RegisterCodeActivity::class.java)
                                intent.putExtra("signUp",reqSignUp)
                                intent.putExtra("code", it.result.success)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetEmailCode>, t: Throwable) {
                Log.d("test: 이메일 코드보내기 실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkEmail(){
        isLoaded = false
        val finalEmail = viewBinding.etEmail.text.toString()
        RetrofitClient.service.checkEmail(finalEmail).enqueue(object : Callback<ResCheckEmail>{
            override fun onResponse(call: Call<ResCheckEmail>, response: Response<ResCheckEmail>) {
                isLoaded = true
                if(response.isSuccessful.not()){
                    Log.d("test: 이메일 중복체크 실패(서버)",response.toString())
                    Toast.makeText(this@RegisterEmailActivity, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else {
                    when (response.code()) {
                        200 -> {
                            emailCheckBtnEnable(false)
                            response.body()?.let {
                                if(it.code == 200){
                                    isLegal = true
                                    Toast.makeText(this@RegisterEmailActivity, "이메일 사용 가늡합니다", Toast.LENGTH_SHORT).show()
                                    nextBtnEnable(true)
                                }else{
                                    isLegal = false
                                    Toast.makeText(this@RegisterEmailActivity, "이메일이 중복되었습니다", Toast.LENGTH_SHORT).show()
                                    viewBinding.etEmail.setBackgroundResource(R.drawable.login_et_failed)
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResCheckEmail>, t: Throwable) {
                isLoaded = true
                Log.d("test: 이메일 중복체크 실패(클라이언트)",t.toString())
                Toast.makeText(this@RegisterEmailActivity, "오류로 인해 서버와 연결을 시도하지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                Toast.makeText(this, "뒤로가기",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnRegisterNext.isSelected != boolean){
            viewBinding.btnRegisterNext.isSelected = boolean
            viewBinding.btnRegisterNext.isEnabled = boolean
            if(boolean){
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.white))
                viewBinding.etEmailChkImg.visibility = View.VISIBLE
            }else{
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.black_500))
                viewBinding.etEmailChkImg.visibility = View.GONE

            }

        }
    }

    private fun emailCheckBtnEnable(status: Boolean){
        if(viewBinding.btnEmailCheck.isSelected != status){
            viewBinding.btnEmailCheck.isSelected = status
            viewBinding.btnEmailCheck.isEnabled = status
            if(status){
                viewBinding.btnEmailCheck.setTextColor(getColor(R.color.green_900))
            }else{
                viewBinding.btnEmailCheck.setTextColor(getColor(R.color.black_500))

            }
        }
    }
}