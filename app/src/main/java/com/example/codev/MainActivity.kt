package com.example.codev

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
        viewBinding.btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterTosActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnLogin.setOnClickListener {
            getToken()
            val intent = Intent(this,MainAppActivity::class.java)
            startActivity(intent)
//            finish() TODO: Testing code
        }

        viewBinding.btnGoogle.setOnClickListener {
//            val intent = Intent(this,MainAppActivity::class.java) TODO: testing code
            val intent = Intent(this,AddNewStudyActivity::class.java)
            startActivity(intent)
//            finish() TODO: Testing code
        }
    }

    private fun getToken(){
        val map : HashMap<String, Any> = HashMap()
        map["co_email"] = "test@naver.com"
        map["co_password"] = "test"

        RetrofitClient.service.signUp(map).enqueue(object: Callback<response>{
            override fun onResponse(call: Call<response>, response: Response<response>) {
                if(response.isSuccessful.not()){
                    return
                }
                response.body()?.let {
                    Log.d("test", "\n${it.toString()}")
                    Log.d("test",it.result.get("accessToken").toString())
                }
            }

            override fun onFailure(call: Call<response>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }

        })
    }
}