package com.codev.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.codev.android.databinding.ActivityRegisterTosBinding

class RegisterTosActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterTosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterTosBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp

        viewBinding.tosRdo1.setOnClickListener {
            if (viewBinding.tosRdo1.isChecked){
                viewBinding.tosRdo2.isChecked = true
                viewBinding.tosRdo3.isChecked = true
                viewBinding.tosRdo4.isChecked = true
                viewBinding.tosRdo5.isChecked = true
                viewBinding.tosRdo6.isChecked = true
                viewBinding.tosRdo7.isChecked = true
                nextBtnEnable(true)
            }
        }

        viewBinding.tosRdo2.setOnClickListener {
            checkNextBtn()
        }

        viewBinding.tosRdo3.setOnClickListener {
            checkNextBtn()
        }

        viewBinding.tosRdo4.setOnClickListener {
            checkNextBtn()
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            val intent = Intent(this,RegisterTypeActivity::class.java)
            intent.putExtra("signUp",reqSignUp)
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

    private fun checkNextBtn() {
        if(viewBinding.tosRdo2.isChecked and viewBinding.tosRdo3.isChecked and viewBinding.tosRdo4.isChecked){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
    }

}