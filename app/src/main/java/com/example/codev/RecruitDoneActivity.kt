package com.example.codev

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitDoneActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecruitDoneBinding
    private lateinit var adapter: AdapterRecruitProfiles
    private lateinit var selectList: ArrayList<ApplicantInfoData>
    private lateinit var roomId: String

    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecruitDoneBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)



        //채팅방 인원
        selectList = intent.getSerializableExtra("selectList") as ArrayList<ApplicantInfoData>
        roomId = intent.getStringExtra("roomId").toString()

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

        splashAnimation() //애니메이션

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

        viewBinding.btnMoveToChat.setOnClickListener {
            conFirmChatRoom(this, roomId, viewBinding.etRoomname.text.toString())
        }
    }

    private fun setAdapter(dataList: ArrayList<ApplicantInfoData>){
        adapter = AdapterRecruitProfiles(this, dataList)
        Log.d("setAdapter :", "AdapterRecruitProfiles 호출 성공")
        Log.d("setAdapter 내부", dataList.toString())
        viewBinding.listProfiles.adapter = adapter
    }


    private fun nextBtnEnable(boolean: Boolean){
        if (viewBinding.btnMoveToChat.isSelected != boolean){
            viewBinding.btnMoveToChat.isSelected = boolean
            viewBinding.btnMoveToChat.isEnabled = boolean
            if(boolean){
                viewBinding.btnMoveToChat.setTextColor(getColor(R.color.white))
            }else{
                viewBinding.btnMoveToChat.setTextColor(getColor(R.color.black_500))
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

    private fun conFirmChatRoom(context: Context, roomId: String, roomTitle: String){
        RetrofitClient.service.confirmChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)),roomId).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방생성 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("stomp: 채팅방 새로 개설되면 안됨", "여기 오면 에러")
                            }
                        }
                        401 ->{
                            Log.d("stomp: 채팅방 이미 존재", "이미 생성")
                            renameAndMoveToChat(context, roomTitle)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: 채팅방생성 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun renameAndMoveToChat(context: Context, roomTitle: String){
        RetrofitClient.service.renameChatRoom(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)), ReqRenameChatRoom(roomId, roomTitle)).enqueue(object:
            Callback<JsonObject> {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 채팅방초대 실패",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            response.body()?.let {
                                Log.d("test: 채팅방초대 성공! ", "\n${it.toString()}")
                                ChatClient.join(context, roomId)
                                val intent = Intent(context, ChatRoomActivity::class.java)
                                intent.putExtra("title", roomTitle)
                                intent.putExtra("roomId", roomId)
                                intent.putExtra("people", selectList.size+1)
                                intent.putExtra("isRead", 0)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("test: 채팅방초대 실패", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}