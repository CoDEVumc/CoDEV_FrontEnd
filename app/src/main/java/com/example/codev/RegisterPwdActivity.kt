package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.codev.databinding.ActivityRegisterPwdBinding

class RegisterPwdActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterPwdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterPwdBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        // 앞서서 인증한 이메일 설정 필요
        // 비밀번호 같은지 확인 필요

        viewBinding.btnRegisterNext.setOnClickListener {
            val intent = Intent(this,RegisterProfileActivity::class.java)
            startActivity(intent)
        }
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

}