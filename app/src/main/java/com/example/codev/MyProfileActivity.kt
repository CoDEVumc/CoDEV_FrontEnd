package com.example.codev

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityMyProfileBinding

class MyProfileActivity:AppCompatActivity() {
    lateinit var viewBinding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarMy.toolbar2.title = ""
        viewBinding.toolbarMy.toolbarText.text = "내 프로필 관리"
        setSupportActionBar(viewBinding.toolbarMy.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }
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