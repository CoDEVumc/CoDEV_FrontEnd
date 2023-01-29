package com.example.codev

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codev.databinding.ActivityRegisterCodeBinding
import kotlin.properties.Delegates


class RegisterCodeActivity:AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterCodeBinding
    private var code by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterCodeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.toolbarRegister.toolbar2.title = ""

        setSupportActionBar(viewBinding.toolbarRegister.toolbar2)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.left2)
        }

        val reqSignUp = intent.getSerializableExtra("signUp") as ReqSignUp
        code = intent.getStringExtra("code")?.toInt()!!
        Log.d("test",code.toString())

        viewBinding.etCode1.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etChange(viewBinding.view1,viewBinding.etCode1,viewBinding.etCode2)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etCode2.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etChange(viewBinding.view2,viewBinding.etCode2,viewBinding.etCode3)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etCode3.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etChange(viewBinding.view3,viewBinding.etCode3,viewBinding.etCode4)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etCode4.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etChange(viewBinding.view4,viewBinding.etCode4,viewBinding.etCode5)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etCode5.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etChange(viewBinding.view5,viewBinding.etCode5,viewBinding.etCode6)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.etCode6.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etChange(viewBinding.view6,viewBinding.etCode6,viewBinding.etCode1)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // 인증 코드 검증 필요
        viewBinding.btnRegisterNext.setOnClickListener {
            if (checkCode()){
                val intent = Intent(this,RegisterPwdActivity::class.java)
                intent.putExtra("signUp",reqSignUp)
                startActivity(intent)
            }else{
                Toast.makeText(this,"입력하신 코드가 알맞지 않습니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCode():Boolean{
        val insertCode:Int = (viewBinding.etCode1.text.toString() + viewBinding.etCode2.text.toString() + viewBinding.etCode3.text.toString() + viewBinding.etCode4.text.toString() + viewBinding.etCode5.text.toString() + viewBinding.etCode6.text.toString()).toInt()
        return code == insertCode
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
        if(!viewBinding.etCode1.text.isNullOrBlank() and !viewBinding.etCode2.text.isNullOrBlank() and !viewBinding.etCode3.text.isNullOrBlank() and !viewBinding.etCode4.text.isNullOrBlank() and !viewBinding.etCode5.text.isNullOrBlank() and !viewBinding.etCode6.text.isNullOrBlank()){
            nextBtnEnable(true)
        }else{
            nextBtnEnable(false)
        }
    }

    private fun etChange(view: View, now_et: EditText, next_et: EditText){
        view.isSelected = !now_et.text.isNullOrBlank()
        checkNextBtn()
        if (!now_et.text.isNullOrBlank() and next_et.text.isNullOrBlank()){
            next_et.requestFocus()
        }
    }

}