package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.codev.addpage.*
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
                            .replace(viewBinding.content.id,RecruitProjectFragment())
                            .commitAllowingStateLoss()

                        //TODO: 김유정 테스트 코드임, 맘대로 삭제 가능
                        val intent = Intent(this@MainAppActivity,AddNewProjectActivity::class.java)
                        startActivity(intent)

                    }
                    R.id.menu_community->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,CommunityFragment())
                            .commitAllowingStateLoss()

                        //TODO: 김유정 테스트 코드임, 맘대로 삭제 가능
                        val intent = Intent(this@MainAppActivity,AddNewStudyActivity::class.java)
                        startActivity(intent)

                    }
                    R.id.menu_home->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,HomeFragment())
                            .commitAllowingStateLoss()
                        //TODO: 김유정 테스트 코드임, 맘대로 삭제 가능
                        val intent = Intent(this@MainAppActivity,PfDetailActivity::class.java)
                        intent.putExtra("id", "48")
                        startActivity(intent)

                    }
                    R.id.menu_chat->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,ChatFragment())
                            .commitAllowingStateLoss()

                        //TODO: 김유정 테스트 코드임, 맘대로 삭제 가능
                        val intent = Intent(this@MainAppActivity,AddPfPageActivity::class.java)
                        intent.putExtra("default", DefaultPf("기본 이름", "2001/09/06", "MALE"))
                        startActivity(intent)

                    }
                    R.id.menu_my->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(viewBinding.content.id,MyFragment())
                            .commitAllowingStateLoss()
                        //TODO: 김유정 테스트 코드임, 맘대로 삭제 가능
                        val intent = Intent(this@MainAppActivity,AddPfPageActivity::class.java)
                        intent.putExtra("pf", EditPF(47.toString(), "titleEdited", "기본 이름", "2001/09/06", "MALE", "새싹", HashMap(), "Inteo Stirng", "hi, my name is abc", listOf("imlink1", "imlink2")))
                        startActivity(intent)
                    }
                }
                true
            }
            selectedItemId = R.id.menu_home
        }

    }
}