package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.codev.databinding.ActivityRegisterTypeBinding

class RegisterTypeActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterTypeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterTypeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        viewBinding.btnBusiness.setOnClickListener {
            viewBinding.btnBusiness.isSelected = true
            viewBinding.btnIndividual.isSelected = false
            nextBtnEnable(true)
        }

        viewBinding.btnIndividual.setOnClickListener {
            viewBinding.btnBusiness.isSelected = false
            viewBinding.btnIndividual.isSelected = true
            nextBtnEnable(true)
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            val intent = Intent(this,RegisterEmailActivity::class.java)
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

    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnRegisterNext.isSelected != boolean){
            viewBinding.btnRegisterNext.isSelected = boolean
            viewBinding.btnRegisterNext.isEnabled = boolean
            if(boolean){
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnRegisterNext.setTextColor(getColor(R.color.black_500))
            }
        }
    }

}