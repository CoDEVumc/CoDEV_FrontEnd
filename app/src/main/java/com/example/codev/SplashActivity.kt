package com.example.codev

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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


        splashAnimation() //애니메이션

    }


    @UiThread
    private fun splashAnimation(){
        val backAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_ic_back)
        viewBinding.icBack.startAnimation(backAnim)
        val frontAnim = AnimationUtils.loadAnimation(this,R.anim.anim_splash_ic_front)
        viewBinding.icFront.startAnimation(frontAnim)

        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

}