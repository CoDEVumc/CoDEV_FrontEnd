package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.codev.databinding.ActivityRegisterProfileBinding

class RegisterProfileActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            val intent = Intent(this,RegisterDoneActivity::class.java)
            finishAffinity()
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