package com.example.codev

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityRegisterDoneBinding


class RegisterDoneActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterDoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterDoneBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnRegisterNext.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}