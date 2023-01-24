package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.codev.addpage.AddNewProjectActivity
//import com.example.codev.addpage.AddNewStudyActivity
import com.example.codev.addpage.AddPfPageActivity
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
                            .replace(viewBinding.content.id,RecruitFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_community->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,CommunityFragment())
                            .commitAllowingStateLoss()
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

        viewBinding.root.setOnClickListener {
            val intent = Intent(this, AddNewProjectActivity::class.java)
            startActivity(intent)
        }
    }
}