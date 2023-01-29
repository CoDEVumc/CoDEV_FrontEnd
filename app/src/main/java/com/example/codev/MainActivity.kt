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
        UserSharedPreferences.clearUser(this)

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

    //토큰 재발급 코드
    private fun refreshToken(context: Context){
        RetrofitClient.service.refreshToken(ReqRefreshToken(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))).enqueue(object: Callback<ResRefreshToken>{
            override fun onResponse(call: Call<ResRefreshToken>, response: Response<ResRefreshToken>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 토큰재발급 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }else{
                    when(response.code()){
                        200->{
                            // 토큰 암호화
                            response.body()?.let {
                                UserSharedPreferences.setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                                Log.d("test: 토큰재발급 성공", "\n${it.toString()}")
                                Log.d("test: 토큰재발급 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)))
                                Log.d("test: 토큰재발급 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResRefreshToken>, t: Throwable) {
                Log.d("test: 토큰재발급 실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}