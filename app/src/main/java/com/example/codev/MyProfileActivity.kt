package com.example.codev

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

        val userinfo = intent.getSerializableExtra("userinfo") as Userinfo
        viewBinding.etName.setText(userinfo.co_name)
        viewBinding.etNickname.setText(userinfo.co_nickName)
        Glide.with(this)
            .load(userinfo.profileImg).circleCrop().fitCenter()
            .into(viewBinding.profileImg)
        viewBinding.count.setText((8 - viewBinding.etNickname.text.length).toString())

        viewBinding.etNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.count.setText((8 - viewBinding.etNickname.text.length).toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //프로필 변경 저장하기 기능 필요
        viewBinding.btnSave.setOnClickListener {

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