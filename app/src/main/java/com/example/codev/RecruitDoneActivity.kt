package com.example.codev

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.example.codev.databinding.ActivityRecruitDoneBinding
import com.example.codev.databinding.ActivityRegisterProfileBinding
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

class RecruitDoneActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitDoneBinding
    private lateinit var adapter: AdapterRecruitProfiles


    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitDoneBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)



        //채팅방 인원
        var selectList = intent.getSerializableExtra("selectList") as ArrayList<ApplicantInfoData>
        if(selectList.size > 4){
            viewBinding.recruitedNum.text = "외 ${selectList.size - 4}명"
            val slicedList = selectList.take(4)
            Log.d("여기 :", selectList.toString())
            setAdapter(slicedList as ArrayList<ApplicantInfoData>)
        }else{
            viewBinding.recruitedNum.isGone = true
            setAdapter(selectList)
        }

        //Log.d("여기 :", selectList.toString())

        //Log.d("From AdapterRecruitProfiles :", selectList.toString())


        //채팅방 이름 입력 -> db에 전달 부분 필요
        viewBinding.etRoomname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (viewBinding.etRoomname.text.length > 0 && viewBinding.etRoomname.text.length <= 30){
                    nextBtnEnable(true)
                }else{
                    nextBtnEnable(false)
                }
                viewBinding.count.text = (30 - viewBinding.etRoomname.text.length).toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })




        splashAnimation() //애니메이션



    }

    private fun setAdapter(dataList: ArrayList<ApplicantInfoData>){
        adapter = AdapterRecruitProfiles(this, dataList)
        Log.d("setAdapter :", "AdapterRecruitProfiles 호출 성공")
        Log.d("setAdapter 내부", dataList.toString())
        viewBinding.listProfiles.adapter = adapter
    }


    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnMovetoChat.isSelected != boolean){
            viewBinding.btnMovetoChat.isSelected = boolean
            viewBinding.btnMovetoChat.isEnabled = boolean
            if(boolean){
                viewBinding.btnMovetoChat.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnMovetoChat.setTextColor(getColor(R.color.black_500))
            }
        }
    }

    @UiThread
    private fun splashAnimation(){
        val upAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_uplayout)
        viewBinding.uplayout.startAnimation(upAnim)
        val downAnim = AnimationUtils.loadAnimation(this,R.anim.anim_splash_downlayout)
        viewBinding.downlayout.startAnimation(downAnim)
    }

}