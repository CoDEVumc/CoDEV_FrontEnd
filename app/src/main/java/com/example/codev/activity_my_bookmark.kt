package com.example.codev

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.codev.databinding.ActivityMyBookmarkBinding
import com.google.android.material.tabs.TabLayout

class activity_my_bookmark : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMyBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_bookmark)


        viewBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e("TAG", "${tab!!.position}")
                when(tab.position){
                    0 ->{ //탭의 왼쪽부터 0 1 2 ...
                        //mListAdapter.filter.filter("프로젝트")
                    }
                    1 -> {
                        //mListAdapter.filter.filter("스터디")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.view.setBackgroundColor(Color.TRANSPARENT)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })





    }



}