package com.example.codev

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityRecruitDetailBinding

class RecruitDetailActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRecruit.toolbar3.title = ""
        //모집글 작성자일시 메뉴 추가(수정, 삭제) 필요
        //스크롤 위치가 이미지 indicator 정도 오면 툴바 배경 흰색으로 변경 필요

        setSupportActionBar(viewBinding.toolbarRecruit.toolbar3)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val imgList = arrayListOf("https://cdn.imweb.me/upload/S201807025b39d1981b0b0/16b98d3e3d30e.jpg",
            "https://cdn.imweb.me/upload/S201807025b39d1981b0b0/16b98d3e3d30e.jpg",
            "https://cdn.imweb.me/upload/S201807025b39d1981b0b0/16b98d3e3d30e.jpg")
        viewBinding.vpImage.adapter = AdapterRecruitDetailImg(this,imgList)
        viewBinding.indicator.attachTo(viewBinding.vpImage)

        //viewBinding.stack.adapter = AdapterRecruitDetailStack(this,)
        //viewBinding.part.adapter = AdapterRecruitDetailPart()
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