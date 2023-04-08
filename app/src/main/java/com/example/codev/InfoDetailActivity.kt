package com.example.codev

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.codev.addpage.*
import com.example.codev.databinding.ActivityCommunityDetailBinding
import com.example.codev.databinding.ActivityRecruitDetailBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat


class InfoDetailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityCommunityDetailBinding
    private var id: Int = -1

    override fun onResume() {
        super.onResume()
        viewBinding.toolbarCommunity.toolbar2.menu.clear()
        id = intent.getIntExtra("id",-1)
        if (id != -1) {
            loadInfoDetail(this, id)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarCommunity.toolbar2.title = ""
        setSupportActionBar(viewBinding.toolbarCommunity.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
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

    private fun loadInfoDetail(context:Context, id: Int){
        RetrofitClient.service.getInfoDetail(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),id).enqueue(object : Callback<ResGetInfoDetail>{
            override fun onResponse(call: Call<ResGetInfoDetail>, response: Response<ResGetInfoDetail>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 조회실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 정보글 조회 성공! ", "\n${it.toString()}")
                                Log.d("test: 정보글 데이터 :", "\n${it.result.Complete}")

                                viewBinding.title.text = it.result.Complete.co_title
                                viewBinding.writerNickname.text = it.result.Complete.co_nickname
                                //viewBinding.writerProfileImg = it.result.Complete.profileImg
                                //viewBinding.writeDate.text = it.result.Complete.updatedAt

                                viewBinding.contentText.text = it.result.Complete.content
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResGetInfoDetail>, t: Throwable) {
                Log.d("test: 조회실패 - RPF > loadData_s(스터디 전체조회)", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

    }
}