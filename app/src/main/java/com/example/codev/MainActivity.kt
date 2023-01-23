package com.example.codev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        // 자동 로그인 방지
        // UserSharedPreferences.clearUser(this)
        val temp = "12345678"
        val temp2 = "12345678"
        val temp_en = AndroidKeyStoreUtil.encrypt(temp)
        val temp2_en = AndroidKeyStoreUtil.encrypt(temp2)
        val temp_de = AndroidKeyStoreUtil.decrypt(temp_en)
        val temp2_de = AndroidKeyStoreUtil.decrypt(temp2_en)
        Log.d("test: 첫 번째 12345678 암호화",temp_en)
        Log.d("test: 두 번째 12345678 암호화",temp2_en)
        Log.d("test: 첫 번째 12345678 복호화",temp_de)
        Log.d("test: 두 번째 12345678 복호화",temp2_de)

        viewBinding.btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterTosActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnLogin.setOnClickListener {
            if (viewBinding.etEmail.text.isNullOrBlank() or viewBinding.etPassword.text.isNullOrBlank()){
                Toast.makeText(this, "아이디와 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }else{
                signIn(this,viewBinding.etEmail.text.toString(),viewBinding.etPassword.text.toString())
            }
        }

        // 자동로그인 확인
        if(checkAutoLogin(this)) {
            val intent = Intent(this,MainAppActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signIn(context:Context, email:String, pwd:String) {
        RetrofitClient.service.signIn(ReqSignIn(email,pwd)).enqueue(object: Callback<ResSignIn>{
            override fun onResponse(call: Call<ResSignIn>, response: Response<ResSignIn>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 로그인 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else{
                    when(response.code()){
                        200->{
                            if(viewBinding.loginAuto.isChecked){
                                UserSharedPreferences.setAutoLogin(context,"TRUE")
                            }
                            // 토큰 암호화
                            response.body()?.let {
                                UserSharedPreferences.setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                                UserSharedPreferences.setUserRefreshToken(context,AndroidKeyStoreUtil.encrypt(it.result.refreshToken))
                                Log.d("test: 로그인 성공", "\n${it.toString()}")
                                Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)))
                                Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))
                            }
                            val intent = Intent(context,MainAppActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResSignIn>, t: Throwable) {
                Log.d("test: 로그인 실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    // AutoLogin 값이 있다면 true, 없다면 false
    private fun checkAutoLogin(context: Context): Boolean {
        return !UserSharedPreferences.getAutoLogin(context).isNullOrBlank()
    }
}