package com.example.codev

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityRegisterBirthBinding

class RegisterBirthActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterBirthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterBirthBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp

        viewBinding.btnMale.setOnClickListener {
            viewBinding.btnMale.isSelected = true
            viewBinding.btnMale.setTextColor(getColor(R.color.green_900))
            viewBinding.btnFemale.isSelected = false
            viewBinding.btnFemale.setTextColor(getColor(R.color.black_500))
        }

        viewBinding.btnFemale.setOnClickListener {
            viewBinding.btnMale.isSelected = false
            viewBinding.btnMale.setTextColor(getColor(R.color.black_500))
            viewBinding.btnFemale.isSelected = true
            viewBinding.btnFemale.setTextColor(getColor(R.color.green_900))
        }

        viewBinding.btnRegisterNext.setOnClickListener {
            val intent = Intent(this,RegisterEmailActivity::class.java)
            intent.putExtra("signUp",reqSignUp)
            startActivity(intent)
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