package com.codev.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.isGone
import com.codev.android.databinding.ActivityRegisterPwdBinding

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

        val regex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[~_!@#${'$'}%^&*()+|=])[A-Za-z\d~_!@#${'$'}%^&*()+|=]{8,}${'$'}""")
        val reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp
        viewBinding.etEmail.setText(reqSignUp.co_email)
        viewBinding.warn1.isGone = true
        viewBinding.warn2.isGone = true

        viewBinding.etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(viewBinding.etPassword.text.matches(regex)){
                    viewBinding.warn1.isGone = true
                    viewBinding.etPassword.setBackgroundResource(R.drawable.login_et)
                }else{
                    viewBinding.warn1.isGone = false
                    viewBinding.etPassword.setBackgroundResource(R.drawable.login_et_failed)
                }
                checkNextBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etPasswordChk.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!viewBinding.etPassword.text.isNullOrBlank() and (viewBinding.etPasswordChk.text.toString() == viewBinding.etPassword.text.toString())){
                    viewBinding.warn2.isGone = true
                    viewBinding.etPasswordChk.setBackgroundResource(R.drawable.login_et)
                    viewBinding.etPasswordChkImg.setImageResource(R.drawable.register_chk_checked)
                    checkNextBtn()
                }else{
                    viewBinding.warn2.isGone = false
                    viewBinding.etPasswordChk.setBackgroundResource(R.drawable.login_et_failed)
                    viewBinding.etPasswordChkImg.setImageResource(R.drawable.register_unchecked)
                    checkNextBtn()
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

    private fun checkNextBtn() {
        if(viewBinding.warn1.isGone and viewBinding.warn2.isGone){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
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