package com.example.codev

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityRecruitApplyBinding

class RecruitApplyActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitApplyBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitApplyBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbarRecruit.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val type = intent.getStringExtra("type")
        viewBinding.toolbarRecruit.toolbar2.title = ""
        if (type == "PROJECT"){
            viewBinding.toolbarRecruit.toolbarText.text = "프로젝트 지원하기"
        }else if (type == "STUDY"){
            viewBinding.toolbarRecruit.toolbarText.text = "스터디 지원하기"
        }

        viewBinding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

            }
        })
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
}