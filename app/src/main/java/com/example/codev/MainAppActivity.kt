package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.codev.databinding.ActivityMainAppBinding

class MainAppActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.content.id,HomeFragment())
            .commitAllowingStateLoss()

        viewBinding.navBottom.run {
            setOnItemSelectedListener {
                when(it.itemId){
                    R.id.menu_recruit->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,RecruitListFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_community->{
//                        supportFragmentManager
//                            .beginTransaction()
//                            .replace(viewBinding.content.id,RecruitDoneActivity())
//                            .commitAllowingStateLoss()
                        val intent = Intent(this@MainAppActivity, RecruitDoneActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.menu_home->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,HomeFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_chat->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,ChatFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_my->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,MyFragment())
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            selectedItemId = R.id.menu_home
        }

    }
}