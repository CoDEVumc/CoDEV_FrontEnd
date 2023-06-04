package com.example.codev

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.example.codev.databinding.ActivityRecruitDoneBinding
import com.example.codev.databinding.ActivityRegisterProfileBinding
import com.example.codev.databinding.ActivitySplashBinding
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

class SplashActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySplashBinding

    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({
            splashAnimation() //애니메이션

            // 일정 시간이 지나면 MainActivity로 이동
            val intent= Intent( this,MainActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()

        }, 5000) // 500  <- 시간 0.5초 이후 실행




    }


    @UiThread
    private fun splashAnimation(){
        val backAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_ic_back)
        viewBinding.icBack.startAnimation(backAnim)
        val frontAnim = AnimationUtils.loadAnimation(this,R.anim.anim_splash_ic_front)
        viewBinding.icFront.startAnimation(frontAnim)



        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()

    }

}