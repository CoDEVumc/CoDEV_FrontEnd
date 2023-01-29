package com.example.codev

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.codev.databinding.ActivityRegisterEmailBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterEmailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterEmailBinding
    private lateinit var reqSignUp: ReqSignUp

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

        reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp

        viewBinding.etEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (viewBinding.etEmail.text.length == 1){
                    nextBtnEnable(true)
                }else if (viewBinding.etEmail.text.isEmpty()){
                    nextBtnEnable(false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

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
            }else{
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.black_500))
            }
        }
    }
}