package com.example.codev

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.codev.databinding.ActivityRecruitApplyListBinding

class RecruitApplyListActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitApplyListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitApplyListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarApply.toolbar2.title = ""
        viewBinding.toolbarApply.toolbarText.text = "지현현황 보기"
        setSupportActionBar(viewBinding.toolbarApply.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }




    }

}