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
import com.example.codev.databinding.ActivityRegisterProfileBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterProfileActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp

        viewBinding.etNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (viewBinding.etNickname.text.length < 8){
                    nextBtnEnable(true)
                }else{
                    nextBtnEnable(false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //프로필 이미지 변경 필요
        viewBinding.btnChange.setOnClickListener {

        }

        viewBinding.btnRegisterNext.setOnClickListener {
            reqSignUp.co_nickName = viewBinding.etNickname.text.toString()
            signUp(this,reqSignUp)
        }
    }

    private fun signUp(context: Context, reqSignUp: ReqSignUp){
        RetrofitClient.service.signUp(reqSignUp).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 회원가입 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let {
                                Log.d("test: 회원가입 성공", "\n${it.toString()}")
                                val intent = Intent(context,RegisterDoneActivity::class.java)
                                finishAffinity()
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: 회원가입 실패2", "[Fail]${t.toString()}")
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