package com.example.codev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import com.example.codev.databinding.ActivityRegisterPwdBinding

class RegisterPwdActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterPwdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterPwdBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp
        viewBinding.etEmail.setText(reqSignUp.co_email)

        // 비밀번호 같은지 확인 필요
        viewBinding.etPasswordChk.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if ((viewBinding.etPassword.text.length > 7) and (viewBinding.etPasswordChk.text.toString() == viewBinding.etPassword.text.toString())){
                    viewBinding.etPasswordChkImg.setImageResource(R.drawable.register_chk_checked)
                    nextBtnEnable(true)
                }else{
                    viewBinding.etPasswordChkImg.setImageResource(R.drawable.register_unchecked)
                    nextBtnEnable(false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.btnRegisterNext.setOnClickListener {
            reqSignUp.co_password = viewBinding.etPassword.text.toString()
            val intent = Intent(this,RegisterProfileActivity::class.java)
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
}