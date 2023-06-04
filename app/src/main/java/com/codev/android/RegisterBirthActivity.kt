package com.codev.android

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codev.android.databinding.ActivityRegisterBirthBinding

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

        viewBinding.etName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkNextBtn()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        viewBinding.btnCalendar.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var dateShowString = "${year}/${month+1}/${dayOfMonth}"
                var dateJsonString = String.format("%d-%02d-%d", year, month + 1, dayOfMonth)
                viewBinding.etBrith.setText(dateShowString)
                viewBinding.etBrith.setBackgroundResource(R.drawable.login_et_focused)
                checkNextBtn()
            }
            var dpd = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        viewBinding.btnMale.setOnClickListener {
            viewBinding.btnMale.isSelected = true
            viewBinding.btnMale.setTextColor(getColor(R.color.green_900))
            viewBinding.btnFemale.isSelected = false
            viewBinding.btnFemale.setTextColor(getColor(R.color.black_500))
            reqSignUp.co_gender = "MALE"
            checkNextBtn()
        }

        viewBinding.btnFemale.setOnClickListener {
            viewBinding.btnMale.isSelected = false
            viewBinding.btnMale.setTextColor(getColor(R.color.black_500))
            viewBinding.btnFemale.isSelected = true
            viewBinding.btnFemale.setTextColor(getColor(R.color.green_900))
            reqSignUp.co_gender = "FEMALE"
            checkNextBtn()
        }

        //이름과 생년월일 필수 체크
        viewBinding.btnRegisterNext.setOnClickListener {
            reqSignUp.co_name = viewBinding.etName.text.toString()
            reqSignUp.co_birth = viewBinding.etBrith.text.toString()
            if (reqSignUp.co_loginType != "CODEV"){
                val intent = Intent(this,RegisterProfileActivity::class.java)
                intent.putExtra("signUp",reqSignUp)
                startActivity(intent)
            }else{
                val intent = Intent(this,RegisterEmailActivity::class.java)
                intent.putExtra("signUp",reqSignUp)
                startActivity(intent)
            }
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

    private fun checkNextBtn() {
        if(viewBinding.btnFemale.isSelected or viewBinding.btnMale.isSelected and !viewBinding.etName.text.isNullOrBlank() and !viewBinding.etBrith.text.isNullOrBlank()){
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